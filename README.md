# Multi-Tenant Flyway
A sample project to handle multi tenant database migrations with Flyway

## Introduction

This is a sample application to demonstrate the use of FlyWay to migrate different databases in one go with different
SQL repositories

## How to use

To make use of this application,

<ul>
<li>Firstly you have to set up database connection properties on the application properties file</li>
<li>By default the db/migrations location present in the classpath will be considered as the root directory for SQL files</li>
<li>To override that you can set the path property with absolute path for the directory with prefix - filesystem:</li>
<li>For each of the datasource configured you must have a directory with datasource name within that you may put all the SQL migrations
related to that database (A sample is already provided here)</li>
</ul>

## Want to Improve?

Just like any other thing in this world this can be further improved and posibilities are limitless!

Please reach out to me on : ejkpac@gmail.com for hugs and bugs! <3
