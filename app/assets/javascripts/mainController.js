app.controller('mainController',['$scope','$resource', function($scope, $resource) {

      $scope.alertScope= function(){
          alert(JSON.stringify($scope.root));
      };

      $scope.descargarConfiguracion = function(nombreNegocio){

        var x = $resource('/negocios/'+nombreNegocio+'/configuracion',{},{
         get:{ method:"GET", headers: {'Accepts': 'application/json; charset=UTF-8'}}
        });

        var y = x.get();

        y.$promise.then(function(data){
            console.log(data);
        });

      };


  }]);