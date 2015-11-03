@(negocio: String)
  app.controller('mainController',['$scope','$resource', function($scope, $resource) {

      $scope.alertScope= function(){
          alert(JSON.stringify($scope.root));
      };

      $scope.cotizar = function(){
        var entityData = {negocio: $scope.root};
        var datos = JSON.stringify(entityData);
        var x = $resource('/cotizar/@negocio',{},{
         save:{ method:"POST", headers: {'Content-Type': 'application/json; charset=UTF-8'}}
        });

        var y = x.save(datos);

        y.$promise.then(function(data){
            console.log(data);
            $scope.root = angular.copy(data);
        });

      };

      $scope.expedir = function(){
        $scope.estadoExpedido=true;
        $scope.cotizar();
      };
  }]);