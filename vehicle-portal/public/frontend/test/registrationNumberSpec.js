'use strict';

/* jasmine spec */


describe('Registration number validation', function () {

    var validCases = ["A9","A99","A999","A9999","AA9","AA99","AA999","AA9999","AAA9","AAA99","AAA999","AAA9999","AAA9A","AAA99A","AAA999A",
                      "9A","9AA","9AAA","99A","99AA","99AAA","999A","999AA","999AAA","9999A","9999AA","A9AAA","A99AAA","A999AAA","AA99AAA"];

    var invalidCases = ["A9A", "AA", "999", "AA9AAA9", "A99999", "AAAA", "AAA9999A"];

    var vehicleFormValidator;

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

//the same as in fleet number, those tests don't really test a thing
    // test valid cases
    angular.forEach(validCases, function(value, key){
        it("should allow format " + value, function() {
            expect(validateMockField('registrationNumber', value)).not.toBe('rule');//'registrationNumberRule');
        });
    });

    // test invalid cases
    angular.forEach(invalidCases, function(value, key){
        it("should disallow format " + value, function() {
            expect(validateMockField('registrationNumber', value)).toBe('rule');//'registrationNumberRule');
        });
    });

});