package ar.edu.tadp.dragonball

import ar.edu.tadp.dragonball.Criterios._
import ar.edu.tadp.dragonball.Movimientos._

case class Guerrero(nombre: String,
                    items: List[Item],
                    energiaMaxima: Int,
                    energia: Int,
                    especie: Especie,
                    estado: Estado,
                    movimientos: List[Movimiento]) {

  def estas(nuevoEstado: Estado) : Guerrero = {
    copy(estado = nuevoEstado)
  }

  def aumentarEnergia(aumento: Int) = {
    copy(energia = (aumento + energia).max(0).min(energiaMaxima))
  }

  def movimientoMasEfectivoContra(oponente: Guerrero)(unCriterio: Criterio): Movimiento = {
    movimientos.maxBy(
      mov => unCriterio(mov(this,oponente))
    )
  }

  def pelearUnRound(movimiento: Movimiento)(oponente: Guerrero): Guerreros = {
    val (atacante, defensor) = movimiento(this, oponente)
    defensor.movimientoMasEfectivoContra(atacante)(quedarConMasEnergia)(atacante, defensor)
  }

  def planDeAtaqueContra (oponente: Guerrero, cantidadDeRounds: Int) (unCriterio: Criterio) :List[Movimiento] = cantidadDeRounds match {
    case 1 => List(movimientoMasEfectivoContra(oponente)(unCriterio))
    case _ =>
      val mov = movimientoMasEfectivoContra(oponente)(unCriterio)
      val (atacanteActual, oponenteActual) = pelearUnRound(mov)(oponente)
      List(mov) ++ atacanteActual.planDeAtaqueContra(oponenteActual, cantidadDeRounds-1)(unCriterio)
  }
}

abstract class Estado

case object Luchando extends Estado
case class Fajado(rounds: Int) extends Estado
case object KO extends Estado
case object Muerto extends Estado