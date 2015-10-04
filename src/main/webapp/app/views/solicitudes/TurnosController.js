app.controller('TurnosController', ["$scope", 'SolicitudService', function($scope, solicitudService){
    $scope.solicitud= {};

    $scope.pageLoaded= false;

    $scope.currentTurno= 1;

    $scope.days= [
        {
            name: "Lunes",
            horas: []
        },
        {
            name: "Martes",
            horas: []
        },
        {
            name: "Miércoles",
            horas: []
        },
        {
            name: "Jueves",
            horas: []
        },
        {
            name: "Viernes",
            horas: []
        },
        {
            name: "Sábado",
            horas: []
        },
        {
            name: "Domingo",
            horas: []
        }
    ];

    $scope.daySelected= $scope.days[0];

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
