# OnlineStore Pet-project

This repository contains example of an online store

The project was created to hone java backend development skills as well as to understand the structure of online commerce

## What is needed for the project

First, you need to clone this repository into your development environment (for example, in InteliJ IDEA)

Next you need to download the PostgreSQL database to your local machine

Also you should create your own database and connect to it via a yaml file which is located in the project main root folder

## Project structure

The project was built on the principle of microservice architecture, therefore it contains the following services:

1. `AdminBLService (ProductBLService)`
2. `OrderBLService`
3. `src (StoreILService)`
4. `UserBLService`

## Launching the application

You must run these 4 services in any order, since the application has FlyWay to autofill the project with content

## Hidden possibilities

The project has an administrator panel: when logging into your account, you must enter the login “Admin”, password - “123123”

After this you will be able to use all the administrator features

## Author's remarks

This project was created from the point of view of backend development, so no time was devoted to adaptive layout - please don't judge for this)
