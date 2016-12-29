# Fern on canvas 

Based on scala js examples [Scala.js](http://www.scala-js.org/).
And following Barnsley Fern iterated function system (IFS)

![Gerating fern](https://github.com/jonasanso/fern-scalajs/raw/master/recording.gif)

## Get started

To get started, run `sbt ~fastOptJS` in this example project. This should
download dependencies and prepare the relevant javascript files. If you open
`localhost:12345/target/scala-2.11/classes/index-dev.html` in your browser you will see the fern being drawn

## The optimized version

Run `sbt fullOptJS` and open up `index-opt.html` for an optimized (~200kb) version
of the final application, useful for final publication.

