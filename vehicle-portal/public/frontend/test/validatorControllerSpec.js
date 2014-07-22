'use strict';

/* jasmine specs for controllers go here */

describe('Validation trigger', function () {
    beforeEach(function(){
        module('dateRangeFields');
        module('vehicle-portal')
    });


    var scope, rootScope, FormValidatorCtr, VehicleFormValidatorMock, MessagesServiceMock, eventMock, boxElement;
    var DateSuffix;

    beforeEach(function () {
        VehicleFormValidatorMock = {
            validate: function (field, value) {
               if(field == 'invalidField'){
                 return 'error';
               }
            }
        };

        MessagesServiceMock = {
            getMessage: function(){
              return 'error';
            }
        };

        eventMock = {
            target: {
                submit: function () {}
            },
            preventDefault : function() {}
        };
    });

    beforeEach(inject(function ($rootScope, $controller, $compile, _DateSuffix_) {
        DateSuffix = _DateSuffix_;
        scope = $rootScope;
        FormValidatorCtr = $controller('form-validator-controller', {
            $scope: scope,
            vehicleFormValidator: VehicleFormValidatorMock,
            messagesService: MessagesServiceMock
        });
        scope.$digest();
    }));


    it('should call validation service', function () {
        spyOn(VehicleFormValidatorMock, 'validate').andCallThrough();
        scope.registerField('fieldName');
        scope.update('fieldName');

        expect(VehicleFormValidatorMock.validate).toHaveBeenCalled();
    });

    it('should call submit if there are no errors', function () {
        spyOn(eventMock.target, 'submit').andCallThrough();
        scope.registerField('fieldName');
        scope.update('fieldName');

        scope.submit(eventMock);
        expect(eventMock.target.submit).toHaveBeenCalled();
    });

    it('should not call submit if there are errors', function () {
        spyOn(eventMock.target, 'submit').andCallThrough();
        scope.registerField('invalidField');
        scope.update('invalidField');

        scope.submit(eventMock);
        expect(eventMock.target.submit).not.toHaveBeenCalled();
    });

    //clearing
    it('should have a method to clear given form fields', function() {
        var element = angular.element('<a clear="vehicleMake">clear</a>');
        scope.vehicleData.vehicleMake = {}
        scope.vehicleData.registrationNumber = {}

        expect(scope.vehicleData.vehicleMake.value).toBe(undefined);
        scope.vehicleData.vehicleMake.value = "skoda" ;
        scope.clear('vehicleMake')
        expect(scope.vehicleData.vehicleMake.value).toBe(undefined);

        scope.vehicleData.vehicleMake.value = "ford"
        expect(scope.vehicleData.vehicleMake.value).toBe('ford');

        scope.clear('vehicleMake')
        scope.vehicleData.registrationNumber.value = "AA11AAA"
        expect(scope.vehicleData.vehicleMake.value).toBe(undefined);
        expect(scope.vehicleData.registrationNumber.value).toBe('AA11AAA');

        var willThrow = function() {
            return scope.vehicleData["field that is not defined"].value;
        };
        scope.clear("field that is not defined");
        expect(willThrow).toThrow();
        expect(scope.vehicleData.vehicleMake.value).toBe(undefined);
        expect(scope.vehicleData.registrationNumber.value).toBe('AA11AAA');

        scope.vehicleData.vehicleMake.value = "ford";
        scope.clear("vehicleMake, registrationNumber")
        expect(scope.vehicleData.vehicleMake.value).toBe(undefined);
        expect(scope.vehicleData.registrationNumber.value).toBe(undefined);
    });

    it('should clear formData from a form', function() {
        scope.vehicleData.vehicleMake = {}
        scope.vehicleData.registrationNumber = {}

        scope.vehicleData.vehicleMake.value = "ford"
        scope.vehicleData.registrationNumber.value = "AA11AAA"

        expect(scope.vehicleData.vehicleMake.value).toBe('ford');
        expect(scope.vehicleData.registrationNumber.value).toBe('AA11AAA');

        scope.clearAll(scope);
        expect(scope.vehicleData.vehicleMake.value).toBe(undefined);
        expect(scope.vehicleData.registrationNumber.value).toBe(undefined);
    });

    // end clearing
});