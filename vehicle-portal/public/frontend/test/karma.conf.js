module.exports = function(config){
  config.set({

    basePath : '../',

    files : [
      'test/lib/angular.js',
      'test/lib/angular-mocks.js',
      'test/lib/jquery-1.9.0.min.js',
      'test/lib/typeahead.changed.min.js',
      'app/**/*.js',
      'test/**/*.js'
    ],

    autoWatch : true,

    frameworks: ['jasmine'],

    browsers : ['PhantomJS'],

    plugins : [
            'karma-jasmine',
            'karma-junit-reporter',
            'karma-phantomjs-launcher'
            ],

    junitReporter : {
      outputFile: 'test_out/unit.xml',
      suite: 'unit'
    }

  });
};