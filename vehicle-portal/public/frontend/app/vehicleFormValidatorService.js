/*global angular */
(function () {
    "use strict";
    var validatorModule = angular.module('validatorModule', []);

    validatorModule.service('vehicleFormValidator', ['DateSuffix',function (DateSuffix) {
        var REG_EXP_BLANK = "^$|";
        var REG_EXP_FLEET_NUMBER = "^[0-9]+[-]?$";
        var REG_EXP_REGISTRATION_NUMBER = "^([a-zA-Z][0-9]{1,3}[a-zA-Z]{3}|[a-zA-Z][0-9]{1,4}|[a-zA-Z]{2}[0-9]{1,4}|[a-zA-Z]{2}[0-9]{2}[a-zA-Z]{3}|[a-zA-Z]{3}[0-9]{1,3}[a-zA-Z]|[a-zA-Z]{3}[0-9]{1,4}|[0-9]{1,3}[a-zA-Z]{1,3}|[0-9]{4}[a-zA-Z]{1,2})$";
        var REG_EXP_VEHICLE_MAKE = "^[a-zA-Z 0-9()+-/]{1,30}";
        var REG_EXP_MONTH_NUMBERS = "^[0,1]?[0-9]{1}$";
        var REG_EXP_YEAR_NUMBERS = "^[0-9]{4}$";

        function stripWhiteSpaces(str) {
            if(str === ''  || str === undefined) return '';
            return str.replace(/\s+/g, '');
        }

        function required(allowSpaces) {
            return function (field) {
                if (field.modified) {
                    if (allowSpaces) {
                        return field.value.length > 0;
                    } else {
                        return stripWhiteSpaces(field.value).length > 0;
                    }
                } else {
                    return true;
                }
            };
        }

        var fleetNumberRule = {
            'required': required(false),
            'rule': function (field) {
                var value, regexResult, lengthResult;
                value = field.value;
                regexResult = new RegExp(REG_EXP_FLEET_NUMBER)
                    .test(value);
                lengthResult = value.length === 6;
                return regexResult && lengthResult;
            }
        };

        var registrationNumberRule = {
            'required': required(true),
            'rule': function (field) {
                var value, re;
                value = stripWhiteSpaces(field.value);
                re = new RegExp(REG_EXP_REGISTRATION_NUMBER);
                return re.test(value);
            }
        };

        // As registrationNumberRule but blanks are allowed
        var registrationNumberFleetRule = {
            'rule': function (field) {
                var value, re;
                value = stripWhiteSpaces(field.value);
                re = new RegExp(REG_EXP_BLANK + REG_EXP_REGISTRATION_NUMBER);
                return re.test(value);
            }
        };

        var vehicleMakeRule = {
            'required': required(true),
            'rule': function (field) {
                var value, re;
                value = stripWhiteSpaces(field.value);
                re = new RegExp(REG_EXP_VEHICLE_MAKE);
                return re.test(value);
            }
        };

        // As registrationNumberRule but blanks are allowed
        var vehicleMakeFleetRule = {
            'rule': function (field) {
                var value, re;
                value = stripWhiteSpaces(field.value);
                re = new RegExp(REG_EXP_BLANK + REG_EXP_VEHICLE_MAKE);
                return re.test(value);
            }
        };

        var monthNumbersRule = {
            'rule': function (field) {
                var value, re, test, iVal;
                value = stripWhiteSpaces(field.value);
                re = new RegExp(REG_EXP_MONTH_NUMBERS);
                test = re.test(value);
                if(test){
                    iVal = parseInt(value);
                    test = (iVal > 0 && iVal <= 12);
                }else if(value === '' || value === undefined){
                    test = true;
                }
                return test;
            }
        };

        var yearNumbersRule = {
            'rule': function (field) {
                var value, re, test, iVal;
                value = stripWhiteSpaces(field.value);
                re = new RegExp(REG_EXP_YEAR_NUMBERS);
                test = re.test(value);
                if(test){
                    iVal = parseInt(value);
                    test = (iVal >= 1900 && iVal <= 2099);
                }else if(!test && (value === '' || value === undefined)){
                    test = true;
                }
                return test;
            }
        };

        var dateRangeValidation = {
            'rule': function (field) {
                return field.rangeValidation(field.name);
            }
        };


        //maybe a master repository of field rules?
        var rules = {};
        rules.fleetNumber = fleetNumberRule;
        rules.registrationNumber = registrationNumberRule;
        rules.vehicleMake = vehicleMakeRule;
        rules.reg = registrationNumberFleetRule;
        rules.mk = vehicleMakeFleetRule;
        rules.mot = dateRangeValidation;
        rules.firstReg = dateRangeValidation;
        rules[DateSuffix.MONTH_FROM] = monthNumbersRule;
        rules[DateSuffix.YEAR_FROM] = yearNumbersRule;
        rules[DateSuffix.YEAR_TO] = yearNumbersRule;
        rules[DateSuffix.MONTH_TO] = monthNumbersRule;
        rules[DateSuffix.YEAR] = yearNumbersRule;
        rules[DateSuffix.MONTH] = monthNumbersRule;


        function validate(field, value) {
            //getting rid of field name if it have '_' in it - date case
            var underscoreIndex = field.indexOf("_");
            field = (underscoreIndex !== -1 )? field.substr(underscoreIndex) : field;

            if (value.modified) {
                var rule, result;
                for (rule in rules[field]) {
                    result = rules[field][rule](value);
                    if (!result) {
                        return rule;
                    }
                }
            }
        }

        return {
            validate: validate
        };
    }]);
}());