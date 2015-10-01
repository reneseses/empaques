app.factory('SolicitudService', ['$http', function($http){
    var service= {};

    service.getCurrentTurno= function(callback){
        $http.get(appContext + 'member/solicitudes/current').success(function(solicitud){
            callback(null, solicitud);
        }).error(function(err){
            callback(err);
        });
    }

    service.getSelectableDays= function(callback){
        
    }

    return service;
}]);
