/**
* Config variables
**/
var views= appContext + 'app/views/';

var app = angular.module('empaquesApp', ['ui.router']);
app.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/home');

    $stateProvider
        .state('home', {
            url: '/home',
            templateUrl: views + 'home/index.html',
            controller: 'HomeController'
        });
});
