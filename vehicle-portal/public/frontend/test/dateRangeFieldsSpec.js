'use strict';

describe("Date checker", function() {
    beforeEach(function(){
        module('dateRangeFields');
        module('vehicle-portal')
    });

    var scope, rootScope, parentScope, FormValidatorCtr, DateRangeCtr;
    var element, DateSuffix, fieldName;


    beforeEach(inject(function ($rootScope, $controller, $compile, _DateSuffix_, _messagesService_, _vehicleFormValidator_) {
        DateSuffix = _DateSuffix_;
        parentScope = $rootScope.$new();

        FormValidatorCtr = $controller('form-validator-controller', {
            $scope: parentScope,
            vehicleFormValidator: _vehicleFormValidator_,
            messagesService: _messagesService_
        });

        fieldName = "mot"
        fillScopeForField(fieldName);

        scope = $rootScope.$new();
        scope.$parent = parentScope;
        scope.$parent.$apply = function() { /*have to mock that way, to stop tests being blocked */;};

        DateRangeCtr = $controller('dateRange', {
            DateSuffix: _DateSuffix_,
            $scope: scope,
            messagesService: _messagesService_
        });

    }));

    var monthFromId, yearFromId, monthToId, yearToId;

    function fillScopeForField(fieldName) {
        parentScope.vehicleData = {}
        monthFromId = fieldName+DateSuffix.MONTH_FROM;
        yearFromId = fieldName+DateSuffix.YEAR_FROM;
        monthToId = fieldName+DateSuffix.MONTH_TO;
        yearToId = fieldName+DateSuffix.YEAR_TO;
        parentScope.registerField(monthFromId);
        parentScope.registerField(yearFromId);
        parentScope.registerField(monthToId);
        parentScope.registerField(yearToId);
        parentScope.vehicleData[fieldName] = {};
    }

   //dateRange
    //TODO place to make tests related to this topic
    describe("dateRange controller isRangeRealistic method", function() {
        it("should return true, when date fields are empty ", function() {
            expect(scope.isRangeRealistic(fieldName)).toBe(true)
        });

        it("should return true for proper date range", function() {
            scope.parentData[monthFromId].value = 11
            scope.parentData[yearFromId].value = 2014
            scope.parentData[monthToId].value = 11
            scope.parentData[yearToId].value = 2015
            expect(scope.isRangeRealistic(fieldName)).toBe(true)
        });

        it("should return false for improper date range", function() {
            scope.parentData[monthFromId].value = 11
            scope.parentData[yearFromId].value = 2015
            scope.parentData[monthToId].value = 11
            scope.parentData[yearToId].value = 2014
            expect(scope.isRangeRealistic(fieldName)).toBe(false)
        });

        it("should return true for empty date range", function() {
            scope.parentData[monthFromId].value = undefined
            scope.parentData[yearFromId].value = undefined
            scope.parentData[monthToId].value = undefined
            scope.parentData[yearToId].value = undefined
            expect(scope.isRangeRealistic(fieldName)).toBe(true)
        });


        it("should return false when month is higher than 12 or lower than 1", function() {
            scope.parentData[monthFromId].value = 20
            scope.parentData[yearFromId].value = 2014
            scope.parentData[monthToId].value = 11
            scope.parentData[yearToId].value = 2014
            expect(scope.isRangeRealistic(fieldName)).toBe(false)

            scope.parentData[monthFromId].value = 0
            scope.parentData[yearFromId].value = 2014
            scope.parentData[monthToId].value = 11
            scope.parentData[yearToId].value = 2014
            expect(scope.isRangeRealistic(fieldName)).toBe(false)
            //month To
            scope.parentData[monthFromId].value = 11
            scope.parentData[yearFromId].value = 2014
            scope.parentData[monthToId].value = 20
            scope.parentData[yearToId].value = 2014
            expect(scope.isRangeRealistic(fieldName)).toBe(false)

            scope.parentData[monthFromId].value = 11
            scope.parentData[yearFromId].value = 2014
            scope.parentData[monthToId].value = -1
            scope.parentData[yearToId].value = 2014
            expect(scope.isRangeRealistic(fieldName)).toBe(false)
        });

        it("should return false when year from is higher than year to", function() {
            scope.parentData[monthFromId].value = 11
            scope.parentData[yearFromId].value = 2015
            scope.parentData[monthToId].value = 11
            scope.parentData[yearToId].value = 2014
            expect(scope.isRangeRealistic(fieldName)).toBe(false)
        });

        it("should return true for valid FROM date but no TO date", function() {
            scope.parentData[monthFromId].value = 11
            scope.parentData[yearFromId].value = 2015
            scope.parentData[monthToId].value = undefined
            scope.parentData[yearToId].value = undefined
            expect(scope.isRangeRealistic(fieldName)).toBe(true)
        });

        it("should return false for valid months but no years", function() {
            scope.parentData[monthFromId].value = 11
            scope.parentData[yearFromId].value = undefined
            scope.parentData[monthToId].value = 12
            scope.parentData[yearToId].value = undefined
            expect(scope.isRangeRealistic(fieldName)).toBe(false)
        });

        it("should return false for valid values but no TO year", function() {
            scope.parentData[monthFromId].value = 11
            scope.parentData[yearFromId].value = 2015
            scope.parentData[monthToId].value = 12
            scope.parentData[yearToId].value = undefined
            expect(scope.isRangeRealistic(fieldName)).toBe(false)
        });

    })

    describe("dateRange controller rangeValidation method", function() {

        it("should have error for fieldName, when one of months is out of range (1<=month<=12)", function() {
            scope.$parent.vehicleData[monthFromId].value = 15
            scope.parentData[yearFromId].value = 1111
            scope.parentData[monthToId].value = 11
            scope.parentData[yearToId].value = 1111
            scope.rangeValidation(fieldName);

            //because of if(isNaN(iFromMonth) || isNaN(iToMonth) ) { return false; }
            //also monthFrom field validation will show correct error in that situation
            expect(scope.parentData[fieldName].error).toBe("Enter a valid date range")

            scope.$parent.vehicleData[monthFromId].value = 11
            scope.parentData[yearFromId].value = 1111
            scope.parentData[monthToId].value = 20
            scope.parentData[yearToId].value = 1111
            scope.rangeValidation(fieldName);

            //because of if(isNaN(iFromMonth) || isNaN(iToMonth) ) { return false; }
            //also monthFrom field validation will show correct error in that situation
            expect(scope.parentData[fieldName].error).toBe("Enter a valid date range")
        })

        it("should have error for fieldName, when date range is incorrect", function() {
            scope.$parent.vehicleData[monthFromId].value = 10
            scope.parentData[yearFromId].value = 2014
            scope.parentData[monthToId].value = 11
            scope.parentData[yearToId].value = 2010
            scope.rangeValidation(fieldName);
            expect(scope.parentData[fieldName].error).toBe("Enter a valid date range")
        });

        it("for correct date Range should not have errors on fieldName", function() {
            scope.$parent.vehicleData[monthFromId].value = 10
            scope.parentData[yearFromId].value = 2013
            scope.parentData[monthToId].value = 11
            scope.parentData[yearToId].value = 2014

            scope.rangeValidation(fieldName);
            expect(scope.parentData[fieldName].error).toBe(undefined)
        });
    });


    //end dateRange


})