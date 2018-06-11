@Login2
Feature: Title of your feature
  I want to use this template for my feature file

  @tag1
  Scenario: Login to Keycloak Application
  Given keycloak user is in Adminstator Page
  When keycloak login with username as "username" and password as "password"
  Then verify keycloak title page
  And LogOff from keycloak

