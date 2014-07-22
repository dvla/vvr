/*global angular */
(function () {
    "use strict";
    var app = angular.module('vehicle-portal', ['validatorModule', 'messagesService', 'dateRangeFields']);

    app.controller('form-validator-controller', function ($scope, vehicleFormValidator, messagesService) {

        // the case when javascript was off and on again after form with invalid data submission during vehicle search
        if (document.getElementById('validationFieldErrorSummary') !== null) {
            console.log('Javascript is on again');
            $event.target.submit();
        }

        $scope.vehicleData = {};

        $scope.registerField = function (field) {
            $scope.$apply(function () {
                $scope.vehicleData[field] = {
                    modified: false,
                    value: $('#' + field).attr('value')
                };
            });
        };

        function updateField(field) {
            var error = vehicleFormValidator.validate(field, $scope.vehicleData[field]);
            if (error) {
                var msg = messagesService.getMessage(field, error);
                $scope.vehicleData[field].error = msg;
                return $scope.vehicleData[field].error;
            }
            else{
                $scope.vehicleData[field].error = error;
                return $scope.vehicleData[field].error;
            }

        }

        $scope.update = updateField;

        $scope.submit = function ($event) {
            var valid = true;
            angular.forEach($scope.vehicleData, function (field, key) {
                $scope.vehicleData[key].modified = true;
                if (updateField(key)) {
                    valid = false;
                }
            });
            if (valid) {
                $event.target.submit();
            } else {
                $event.preventDefault();
            }

        };

        $scope.clearAll = function() {
            angular.forEach($scope.vehicleData, function(value, key) {
               clearField(key);
            });
        };

        $scope.clear = function (sToClear){
            var aToClear = sToClear.split(",");
            for(var n=0;n < aToClear.length; n++){
                clearField(aToClear[n].trim());
            }
        };

        function clearField(fieldName) {
            var field = $scope.vehicleData[fieldName];
            if(field !== undefined) {
                field.value = undefined;
                field.modified = false;
                field.error = undefined;

                $("#"+fieldName).val('');
                $("#"+fieldName).typeahead('setQuery', '');
                $scope.$apply(); //to remove error message
            } else if($("#"+fieldName).prop('checked')){
                $("#"+fieldName).prop('checked', false);
            }
        }
    });

    app.directive('validatedfield', function ($compile) {
        return {
            restrict: 'A',
            replace: false,
            terminal: true, //this setting is important, see explanation below
            priority: 1000, //this setting is important, see explanation below
            compile: function (element, attrs) {
                element.attr('ng-model', 'vehicleData.' + attrs.id + '.value');
                element.attr('ng-change', 'vehicleData.' + attrs.id + '.modified = true');
                element.attr('ng-blur', "update('" + attrs.id + "')");
                element.removeAttr('validatedfield'); //remove the attribute to avoid indefinite loop
                return {
                    pre: function preLink(scope, iElement, iAttrs, controller) {},
                    post: function postLink(scope, iElement, iAttrs, controller) {
                        setTimeout(function () {
                            scope.registerField(attrs.id);
                        });

                        $compile(iElement)(scope);
                    }
                };
            }
        };
    });

    app.directive('validation', function ($compile) {
        return {
            restrict: 'A',
            replace: false,
            terminal: true, //this setting is important, see explanation below
            priority: 1000, //this setting is important, see explanation below
            compile: function (element, attrs) {
                element.attr('ng-show', 'vehicleData.' + attrs.validation + '.error');
                element.attr('ng-bind', 'vehicleData.' + attrs.validation + '.error');
                element.removeClass('hidden');
                element.removeAttr('validation'); //remove the attribute to avoid indefinite loop
                return {
                    pre: function preLink(scope, iElement, iAttrs, controller) {},
                    post: function postLink(scope, iElement, iAttrs, controller) {
                        $compile(iElement)(scope);
                    }
                };
            }
        };
    });

    app.directive('validationgroup', function ($compile) {
        return {
            restrict: 'A',
            replace: false,
            terminal: true, //this setting is important, see explanation below
            priority: 1000, //this setting is important, see explanation below
            compile: function (element, attrs) {
                var aValidGroups = (attrs.validationgroup !== undefined)? attrs.validationgroup.split(",") : "";
                var prefix = "", sFieldsToCompare = "";
                angular.forEach(aValidGroups, function(value, key) {
                   sFieldsToCompare += prefix + 'vehicleData.' + value + '.error';
                   prefix = " || ";
                });
                element.attr('ng-class', '{"validation group": '+sFieldsToCompare+' }');
                element.removeAttr('validationgroup'); //remove the attribute to avoid indefinite loop
                return {
                    pre: function preLink(scope, iElement, iAttrs, controller) {},
                    post: function postLink(scope, iElement, iAttrs, controller) {
                        $compile(iElement)(scope);
                    }
                };
            }
        };
    });

    app.directive("jsHidden", function () {
        return {
            restrict: 'C',
            link: function (scope, element, attrs, ctrl) {
                element.removeClass('js-hidden');
            }
        };
    });

    app.directive("clear", function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs, ctrl) {
                element.on('mousedown', function(event) { scope.clear(attrs.clear); });
            }
        };
    });

    app.directive("clearForm", function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs, ctrl) {
                element.on('click', function() { scope.clearAll(); });
            }
        };
    });


}());
//                                 
//Explanation why we have to set terminal: true and priority: 1000 (a high number):
//                                 
//When the DOM is ready, angular walks the DOM to identify all registered directives and compile the directives one by one based on priority if these directives are on the same element. We set our custom directive's priority to a high number to ensure that it will be compiled first and with terminal: true, the other directives will be skipped after this directive is compiled.
//                                 
//When our custom directive is compiled, it will modify the element by adding directives and removing itself and use $compile service to compile all the directives (including those that were skipped).
//                                 
//If we don't set terminal:true and priority: 1000, there is a chance that some directives are compiled before our custom directive. And when our custom directive uses $compile to compile the element => compile again the already compiled directives. This will cause unpredictable behavior especially if the directives compiled before our custom directive already transformed the DOM.