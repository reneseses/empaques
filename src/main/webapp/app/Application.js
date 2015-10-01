/**
* Config variables
**/
var views= appContext + 'app/views/';

var app = angular.module('empaquesApp', ['ui.router', 'ngAnimate']);
app.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/home');

    $stateProvider
        .state('home', {
            url: '/home',
            templateUrl: views + 'home/index.html',
            controller: 'HomeController'
        })
        .state('solicitudes/turnos', {
            url: '/solicitudes/turnos',
            templateUrl: views + 'solicitudes/turnos.html',
            controller: 'TurnosController'
        });
});
