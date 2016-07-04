const gulp    = require('gulp');
const seq     = require('run-sequence');
const webpack = require('webpack-stream');
const uglify  = require('gulp-uglify');
const del     = require('del');

gulp.task('default', (cb) => seq('clean', 'build', cb));

gulp.task('watch', ['default'], () => {
  gulp.watch('src/main/js/**/*', ['webpack']);
  gulp.watch('src/main/static/**/*', ['static']);
  gulp.watch('src/main/groovlet/**/*', ['groovlet']);
  gulp.watch('src/main/config/**/*', ['config']);
});

gulp.task('build', ['webpack', 'vendor', 'static', 'groovlet', 'config']);

gulp.task('webpack', () =>
  gulp.src('src/main/js/main.jsx')
    .pipe(webpack({
      output: { filename: 'main.js' },
      externals: { react: 'React' },
      module: {
        loaders: [
          { test: /\.jsx$/, loader: 'babel-loader?presets[]=es2015&presets[]=react' },
          { test: /\.json$/, loader: 'json-loader' },
          { test: /\.less$/, loader: 'style!css!less?compress' }
        ]
      }
    }))
    .pipe(uglify())
    .pipe(gulp.dest('build/assets')));

gulp.task('vendor', () =>
  gulp.src([
    'node_modules/react/dist/react.min.js',
    'node_modules/bootswatch/cosmo/bootstrap.min.css',
    'node_modules/bootswatch/fonts**/*',
  ]).pipe(gulp.dest('build/assets')));

gulp.task('static', () =>
  gulp.src('src/main/static/**/*').pipe(gulp.dest('build/assets')));

gulp.task('groovlet', () =>
  gulp.src('src/main/groovlet/**/*').pipe(gulp.dest('build/assets/WEB-INF/groovy')));

gulp.task('config', () =>
  gulp.src('src/main/config/**/*').pipe(gulp.dest('build/assets/WEB-INF')));

gulp.task('clean', (cb) => del([
  'build/assets/**',
  '!build/assets',
  '!build/assets/WEB-INF',
  // JARs and classes managed by Gradle
  '!build/assets/WEB-INF/lib/**',
  '!build/assets/WEB-INF/classes/**',
  // persistent data
  '!build/assets/WEB-INF/appengine-generated/**',
], cb));
