app.controller('TurnosController', ["$scope", 'SolicitudService', function($scope, solicitudService){
    $scope.solicitud= {};

    $scope.pageLoaded= false;

    solicitudService.getCurrentTurno(function(err, solicitud){
        if(err){
            console.log(err);
        }else{
            $scope.solicitud= solicitud;
            console.log(solicitud);
        }

        $scope.pageLoaded=true;
    })

}]);
