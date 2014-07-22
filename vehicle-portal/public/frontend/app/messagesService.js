/*global angular */
(function () {
    "use strict";
    var messagesService = angular.module('messagesService', []);

    messagesService.service('messagesService', ['DateSuffix', function (DateSuffix) {

        /* Note: to avoid duplicating text to incorporate fleet specific fields, we'll
           reuse the ones below and let the getMessage function deal with it */
        var messages = {};
            messages['fleetNumber.rule'] = 'Enter a valid fleet number';
            messages['fleetNumber.required'] = 'Enter your fleet number';
            messages['registrationNumber.rule'] = 'Enter a valid vehicle registration number';
            messages['registrationNumber.required'] = 'Enter your vehicle registration number';
            messages['vehicleMake.required'] = 'Enter your vehicle make';
            messages['vehicleMake.rule'] = 'Enter a valid vehicle make';
            messages['reg.rule'] = 'Enter a valid vehicle registration number';
            messages['mk.rule'] = 'Enter a valid vehicle make';
            messages['mot.rule'] = 'Enter a valid MOT date range';
            messages['firstReg.rule'] = 'Enter a valid first registration date range';
            messages[DateSuffix.YEAR_FROM+'.rule'] = 'Enter valid year from';
            messages[DateSuffix.MONTH_FROM+'.rule'] = 'Enter valid month from';
            messages[DateSuffix.MONTH_TO+'.rule'] = 'Enter valid month to';
            messages[DateSuffix.YEAR_TO+'.rule'] = 'Enter valid year to';
            messages[DateSuffix.MONTH+'.rule'] = 'Enter valid month';
            messages[DateSuffix.YEAR+'.rule'] = 'Enter valid year';
            messages['range.rule'] = 'Enter a valid date range';


        return {
            getMessage: function (key, message, skipPart) {
                /* Strip the text preceding any underscore from the key so that we can make use of the messages above
                   e.g mot_monthFrom => _monthFrom
                 */
                var underscoreIndex = key.indexOf("_");
                if (underscoreIndex !== -1) {
                    key = key.replace(key.substr(0,underscoreIndex), "");
                }
                return messages[key + '.' + message];
            }
        };
    }]);
}());