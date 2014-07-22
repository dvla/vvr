/*global angular */
(function () {
    "use strict";
    var appModule = angular.module('dateRangeFields', []);

    appModule.service('DateSuffix', function () {
        return {
            MONTH_FROM : "_monthFrom",
            YEAR_FROM : "_yearFrom",
            MONTH_TO : "_monthTo",
            YEAR_TO : "_yearTo",
            MONTH : "_month",
            YEAR : "_year"
        };
    });

    appModule.controller('dateRange', ['DateSuffix', '$scope', 'messagesService', function (DateSuffix, $scope, messagesService) {

         $scope.parentData = $scope.$parent.vehicleData;

         $scope.rangeValidation = function(fieldName) {
              if (!$scope.isRangeRealistic(fieldName)) {
                  var msg = messagesService.getMessage("range", 'rule');
                  $scope.parentData[fieldName].error = msg;
              }
              else{
                  $scope.parentData[fieldName].error = undefined;
              }
              $scope.$parent.$apply();
         };

         $scope.isRangeRealistic = function(fieldName) {
              var iFromMonth = getFromScopeAsInt(fieldName,DateSuffix.MONTH_FROM);
              var iFromYear = getFromScopeAsInt(fieldName,DateSuffix.YEAR_FROM);
              var iToMonth = getFromScopeAsInt(fieldName,DateSuffix.MONTH_TO);
              var iToYear = getFromScopeAsInt(fieldName,DateSuffix.YEAR_TO);

              // if month range is invalid, set to NaN
              iFromMonth = ((!isNaN(iFromMonth)) && iFromMonth <= 12 && iFromMonth >= 1)? iFromMonth : NaN;
              iToMonth = ((!isNaN(iToMonth)) && iToMonth <= 12 && iToMonth >= 1)? iToMonth : NaN;

              // return true only if...
              //  all fields empty
              if (isNaN(iFromMonth) && isNaN(iFromYear) && isNaN(iToMonth) && isNaN(iToYear)) { return true; }
              //  FROM values present but no TO values
              if ( (!isNaN(iFromMonth) && !isNaN(iFromYear)) && isNaN(iToMonth) && isNaN(iToYear)) { return true; }
              //  FROM cannot be later than TO
              if ( (iFromYear < iToYear) || (iFromYear == iToYear && iFromMonth <= iToMonth) ) { return true; }

              return false;
         };


         function getFromScopeAsInt(fieldName,suffix){
            return parseInt($scope.parentData[fieldName+suffix].value);
         }
    }]);

    appModule.directive("celljumptarget", function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs, ctrl) {
                element.on('keyup', function(e) {
                    var key = e.charCode || e.keyCode || 0;
                    if(element.val().length == attrs.maxlength && (key >= 48 && key <= 57)) {
                        angular.element('#'+ attrs.celljumptarget).focus();
                    }
                });
            }
        };
    });

    appModule.directive("digitsonly", function () {
        // allow backspace, tab, delete, enter, arrows, numbers and keypad numbers ONLY
        // home, end, period, and numpad decimal
        return {
            restrict: 'A',
            link: function (scope, element, attrs, ctrl) {
                 element.on('keydown', function(event){
                       var key = event.charCode || event.keyCode || 0;
                       if(!(
                            key == 8 || //backspace
                            key == 9 || //tab
                            key == 13 || //enter
                            key == 46 || //delete
                            key == 110 || //decimal point
                            key == 190 || //period
                            (key >= 35 && key <= 40) || //end, home, left, right, up, down
                            (key >= 48 && key <= 57) || //0-9
                            (key >= 96 && key <= 105))) //keypad 0-9
                            {
                       event.preventDefault();
                       }
                 });
            }
        };
     });

    appModule.directive("datesChecker", ['DateSuffix', function (DateSuffix) {
         return {
             restrict: 'A',
             link: function (scope, element, attrs) {

                 var fieldName = attrs.datesChecker;
                 scope.$parent.vehicleData[fieldName] = {
                    name: fieldName,
                    error: undefined,
                    rangeValidation: scope.isRangeRealistic
                 };
            }

         };
    }]);

}());