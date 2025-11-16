// static/js/dashboard.js
class DashboardManager {
    constructor() {
        this.charts = {};
        this.init();
    }

    init() {
        this.setDefaultDates();
        this.loadDashboardData();
        this.bindEvents();
    }

    setDefaultDates() {
        const endDate = new Date().toISOString().split('T')[0];
        const startDate = new Date();
        startDate.setDate(startDate.getDate() - 30);
        const startDateStr = startDate.toISOString().split('T')[0];

        document.getElementById('startDate').value = startDateStr;
        document.getElementById('endDate').value = endDate;
        this.updatePeriodDisplay(startDateStr, endDate);
    }

    bindEvents() {
        document.getElementById('dashboardFilters').addEventListener('submit', (e) => {
            e.preventDefault();
            this.loadDashboardData();
        });
    }

    async loadDashboardData() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        this.showLoading();
        this.hideError();

        try {
            const response = await axios.get('/api/admin/dashboard/data', {
                params: { startDate, endDate }
            });

            console.log('API Response:', response.data);

            if (response.data) {
                this.renderDashboard(response.data);
                this.updatePeriodDisplay(startDate, endDate);
                this.showContent();
            } else {
                throw new Error('No data received from server');
            }

        } catch (error) {
            console.error('Error loading dashboard data:', error);
            this.showError('Ошибка загрузки данных: ' + (error.response?.data?.message || error.message));
            this.hideContent();
        } finally {
            this.hideLoading();
        }
    }

    renderDashboard(data) {
        if (!data) {
            console.error('No data provided to renderDashboard');
            this.showNoData();
            return;
        }

        console.log('Rendering dashboard with data:', data);

        this.renderMetrics(data.salesMetrics, data.userMetrics);
        this.renderCharts(data);
        this.renderTopProducts(data.topProducts);
        this.renderInventoryMetrics(data.inventoryMetrics);
    }

    renderMetrics(salesMetrics, userMetrics) {
        const metricsRow = document.getElementById('metricsRow');

        if (!salesMetrics || !userMetrics) {
            console.error('Missing metrics data');
            metricsRow.innerHTML = '<div class="col-12"><p class="text-muted text-center">Нет данных о метриках</p></div>';
            return;
        }

        const metricsHtml = `
        <div class="col-md-3 col-6 mb-3">
            <div class="card metric-card bg-primary text-white">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div class="flex-grow-1">
                            <h6 class="card-title">Общая выручка</h6>
                            <div class="metric-value">BYN${this.formatCurrency(salesMetrics.totalRevenue || 0)}</div>
                            <div class="metric-growth ${(salesMetrics.revenueGrowth || 0) >= 0 ? 'growth-positive' : 'growth-negative'}">
                                <i class="fas ${(salesMetrics.revenueGrowth || 0) >= 0 ? 'fa-arrow-up' : 'fa-arrow-down'} me-1"></i>
                                <span>${Math.abs(salesMetrics.revenueGrowth || 0).toFixed(1)}%</span>
                            </div>
                        </div>
                        <i class="fas fa-chart-line fa-lg opacity-50 ms-2"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-6 mb-3">
            <div class="card metric-card bg-success text-white">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div class="flex-grow-1">
                            <h6 class="card-title">Всего заказов</h6>
                            <div class="metric-value">${salesMetrics.totalOrders || 0}</div>
                            <div class="metric-growth growth-positive">
                                <i class="fas fa-trend-up me-1"></i>
                            </div>
                        </div>
                        <i class="fas fa-shopping-bag fa-lg opacity-50 ms-2"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-6 mb-3">
            <div class="card metric-card bg-info text-white">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div class="flex-grow-1">
                            <h6 class="card-title">Средний чек</h6>
                            <div class="metric-value">BYN${this.formatCurrency(salesMetrics.averageOrderValue || 0)}</div>
                            <div class="metric-growth growth-positive">
                                <i class="fas fa-arrow-up me-1"></i>
                            </div>
                        </div>
                        <i class="fas fa-receipt fa-lg opacity-50 ms-2"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-6 mb-3">
            <div class="card metric-card bg-warning text-white">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div class="flex-grow-1">
                            <h6 class="card-title">Активные пользователи</h6>
                            <div class="metric-value">${userMetrics.activeUsers || 0}</div>
                            <div class="metric-growth growth-positive">
                                <i class="fas fa-users me-1"></i>
                            </div>
                        </div>
                        <i class="fas fa-user-check fa-lg opacity-50 ms-2"></i>
                    </div>
                </div>
            </div>
        </div>
    `;

        metricsRow.innerHTML = metricsHtml;
    }

    renderCharts(data) {
        this.destroyExistingCharts();

        this.renderSalesRevenueChart(data.salesChart, data.revenueChart);

        this.renderCategoryChart(data.categoryDistribution);

        this.renderConversionChart(data.conversionFunnel);
    }

    renderSalesRevenueChart(salesData, revenueData) {
        const ctx = document.getElementById('salesRevenueChart');
        if (!ctx || !salesData || salesData.length === 0) {
            this.showNoChartData('salesRevenueChart', 'Нет данных для графика продаж');
            return;
        }

        const labels = salesData.map(item => {
            try {
                if (item.date) {
                    const date = new Date(item.date);
                    return date.toLocaleDateString('ru-RU');
                }
                return item.label || '';
            } catch (e) {
                return item.label || '';
            }
        });

        this.charts.salesRevenue = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Продажи (шт)',
                        data: salesData.map(item => item.value || 0),
                        borderColor: '#3b82f6',
                        backgroundColor: 'rgba(59, 130, 246, 0.1)',
                        yAxisID: 'y',
                        tension: 0.4,
                        fill: true
                    },
                    {
                        label: 'Выручка (BYN)',
                        data: revenueData ? revenueData.map(item => item.value || 0) : [],
                        borderColor: '#10b981',
                        backgroundColor: 'rgba(16, 185, 129, 0.1)',
                        yAxisID: 'y1',
                        tension: 0.4,
                        fill: true
                    }
                ]
            },
            options: {
                responsive: true,
                interaction: { mode: 'index', intersect: false },
                scales: {
                    y: {
                        type: 'linear',
                        display: true,
                        position: 'left',
                        title: { display: true, text: 'Количество продаж' }
                    },
                    y1: {
                        type: 'linear',
                        display: true,
                        position: 'right',
                        title: { display: true, text: 'Выручка (BYN)' },
                        grid: { drawOnChartArea: false },
                    },
                }
            }
        });
    }

    renderCategoryChart(categoryData) {
        const ctx = document.getElementById('categoryChart');
        if (!ctx || !categoryData || categoryData.length === 0) {
            this.showNoChartData('categoryChart', 'Нет данных для распределения по категориям');
            return;
        }

        this.charts.category = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: categoryData.map(item => item.category || 'Другое'),
                datasets: [{
                    data: categoryData.map(item => item.percentage || 0),
                    backgroundColor: [
                        '#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6',
                        '#06b6d4', '#84cc16', '#f97316', '#ec4899', '#64748b'
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom' },
                    tooltip: {
                        callbacks: {
                            label: (context) => {
                                return `${context.label}: ${context.parsed.toFixed(1)}%`;
                            }
                        }
                    }
                }
            }
        });
    }

    renderConversionChart(conversionData) {
        const ctx = document.getElementById('conversionChart');
        if (!ctx || !conversionData || conversionData.length === 0) {
            this.showNoChartData('conversionChart', 'Нет данных для воронки конверсии');
            return;
        }

        this.charts.conversion = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: conversionData.map(item => item.stage || ''),
                datasets: [{
                    label: 'Конверсия (%)',
                    data: conversionData.map(item => item.rate || 0),
                    backgroundColor: '#8b5cf6'
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        title: { display: true, text: 'Процент (%)' }
                    }
                }
            }
        });
    }

    renderTopProducts(products) {
        const tbody = document.getElementById('topProductsBody');
        if (!tbody) return;

        if (!products || products.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Нет данных о товарах</td></tr>';
            return;
        }

        tbody.innerHTML = products.map(product => `
            <tr>
                <td>${product.productName || 'Неизвестный товар'}</td>
                <td>${product.unitsSold || 0}</td>
                <td>BYN${this.formatCurrency(product.revenue || 0)}</td>
            </tr>
        `).join('');
    }

    renderInventoryMetrics(inventoryMetrics) {
        const container = document.getElementById('inventoryMetrics');
        if (!container) return;

        if (!inventoryMetrics) {
            container.innerHTML = '<div class="col-12"><p class="text-muted text-center">Нет данных о запасах</p></div>';
            return;
        }

        container.innerHTML = `
            <div class="col-md-4">
                <div class="card metric-card">
                    <div class="card-body">
                        <h6 class="card-title">Товары в каталоге</h6>
                        <div class="metric-value text-primary">${inventoryMetrics.totalProducts || 0}</div>
                        <small class="text-muted">Всего SKU</small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card metric-card">
                    <div class="card-body">
                        <h6 class="card-title">Низкий запас</h6>
                        <div class="metric-value text-warning">${inventoryMetrics.lowStockItems || 0}</div>
                        <small class="text-muted">Меньше 10 единиц</small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card metric-card">
                    <div class="card-body">
                        <h6 class="card-title">Нет в наличии</h6>
                        <div class="metric-value text-danger">${inventoryMetrics.outOfStockItems || 0}</div>
                        <small class="text-muted">Требует пополнения</small>
                    </div>
                </div>
            </div>
        `;
    }

    showNoChartData(chartId, message) {
        const chartElement = document.getElementById(chartId);
        if (chartElement) {
            chartElement.parentElement.innerHTML += `
                <div class="text-center text-muted mt-4">
                    <i class="fas fa-chart-bar fa-2x mb-2"></i>
                    <p>${message}</p>
                </div>
            `;
        }
    }

    destroyExistingCharts() {
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });
        this.charts = {};
    }

    formatCurrency(value) {
        return new Intl.NumberFormat('ru-RU', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(value);
    }

    updatePeriodDisplay(startDate, endDate) {
        document.getElementById('periodDisplay').textContent = `${startDate} - ${endDate}`;
    }

    showLoading() {
        document.getElementById('loadingSpinner').style.display = 'block';
    }

    hideLoading() {
        document.getElementById('loadingSpinner').style.display = 'none';
    }

    showContent() {
        document.getElementById('dashboardContent').style.display = 'block';
        document.getElementById('noDataMessage').style.display = 'none';
    }

    hideContent() {
        document.getElementById('dashboardContent').style.display = 'none';
    }

    showNoData() {
        document.getElementById('noDataMessage').style.display = 'block';
        document.getElementById('dashboardContent').style.display = 'none';
    }

    showError(message) {
        const errorAlert = document.getElementById('errorAlert');
        const errorMessage = document.getElementById('errorMessage');
        errorMessage.textContent = message;
        errorAlert.style.display = 'block';
    }

    hideError() {
        document.getElementById('errorAlert').style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new DashboardManager();
});