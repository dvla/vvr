'use strict';

/* jasmine spec */


describe('Fleet number validation', function () {

    var validCases = ["123456", "12345-"];

    var invalidCases = ["1234567", "1234--", "123456-", "12 123-", "12 1234", "1A2345"];

    var vehicleFormValidator;
    var dateSuffix;

    function validateMockField(field, value){
        return vehicleFormValidator.validate(field, {
            'modified': true,
            'value':value
            });
    }

    beforeEach(function () {
        // load the module.
        module('dateRangeFields');
        module('validatorModule');

        // inject your service for testing.
        // The _underscores_ are a convenience thing
        // so you can have your variable name be the
        // same as your injected service.
        inject(function(_vehicleFormValidator_) {
            vehicleFormValidator = _vehicleFormValidator_;
        });


    });

//those tests aren't good right now, because all rule ale named 'rule'
//I may say that it wasn't good even before current change
    // test valid cases
    angular.forEach(validCases, function(value, key){
        it("should allow " + value, function() {
            expect(validateMockField('fleetNumber', value)).not.toBe('rule');//'fleetNumberRule'); - because we changed all to 'rule" generally
        });
    });

    // test invalid cases
    angular.forEach(invalidCases, function(value, key){
        it("should disallow " + value, function() {
            expect(validateMockField('fleetNumber', value)).toBe('rule');//'fleetNumberRule'); - because we changed all to 'rule" generally
        });
    });

});