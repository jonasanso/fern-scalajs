package com.fern

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, html}

import scala.util.Random

case class Matrix(a1: BigDecimal, a2: BigDecimal,
                  b1: BigDecimal, b2: BigDecimal) {
  def *(p: Point) = Point(a1 * p.x + a2 * p.y, b1 * p.x + b2 * p.y)
}

case class Point(x: BigDecimal, y: BigDecimal) {
  def +(p: Point) = Point(x + p.x, y + p.y)
}

sealed trait Transformation {
  def apply(p: Point): Point
}

sealed trait AffineTransformation extends Transformation {
  def base: Matrix

  def add: Point

  def apply(p: Point): Point =
    base * p + add
}

object Stem extends AffineTransformation {
  override val base = Matrix(0, 0, 0, 0.16)
  override val add = Point(0, 0)
}

object SmallerLeaflets extends AffineTransformation {
  override val base = Matrix(0.85, 0.04, -0.04, 0.85)
  override val add = Point(0, 1.6)
}

object LargestLeftLeaflet extends AffineTransformation {
  override val base = Matrix(0.2, -0.26, 0.23, 0.22)
  override val add = Point(0, 1.6)
}

object LargestRightLeaflet extends AffineTransformation {
  override val base = Matrix(-0.15, 0.28, 0.26, 0.24)
  override val add = Point(0, 0.44)
}

object BarnsleyFernTransformation extends Transformation {
  override def apply(p: Point): Point =
    Random.nextInt(100) match {
      case r if r < 2 => Stem(p)
      case r if r < 86 => SmallerLeaflets(p)
      case r if r < 93 => LargestLeftLeaflet(p)
      case _ => LargestRightLeaflet(p)
    }
}

case class CanvasCoordinates(x: Int, y: Int)

class Canvas2D[T](ctx: CanvasRenderingContext2D, size: BigDecimal, f: T => CanvasCoordinates) {

  def background(): Unit = {
    ctx.fillStyle = "white"
    ctx.fillRect(0, 0, size.toInt, size.toInt)
    ctx.fillStyle = s"rgb(80, 118, 66)"
  }

  def paint(p: T): Unit = paint(f(p))

  private def paint(cord: CanvasCoordinates): Unit = {
    ctx.fillRect(cord.x, cord.y, 1, 1)
  }
}

object Canvas2D {
  def apply(canvas: html.Canvas, size: BigDecimal): Canvas2D[Point] = {
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    def transform(p: Point): CanvasCoordinates = {
      val x = size.toInt / 2 + (p.x * size / 10).toInt
      val y = size.toInt - (p.y * size / 10).toInt

      CanvasCoordinates(x, y)
    }

    new Canvas2D[Point](ctx, size, transform)
  }
}

class IFS[T](init: T, f: T => T, time: Int) {

  def run(): Unit = {
    dom.window.setTimeout(() => next(init), time)
  }

  private def next(t: T): Unit = {
    val n = f(t)
    dom.window.setTimeout(() => next(n), time)
  }
}

@JSExport
object FernScalaJS {

  val size = BigDecimal(500)
  val time = 1

  @JSExport
  def main(canvas: html.Canvas): Unit = {

    val canvas2D = Canvas2D(canvas, size)

    canvas2D.background()

    def paintNext(p: Point): Point = {
      canvas2D.paint(p)
      BarnsleyFernTransformation(p)
    }

    new IFS(Point(0, 0), paintNext, 5).run()
  }

}
