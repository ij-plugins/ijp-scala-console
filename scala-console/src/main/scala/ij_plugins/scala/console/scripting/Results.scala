package ij_plugins.scala.console.scripting

object Results {
  /** A result from the Interpreter interpreting one line of input. */
  abstract sealed class Result

  /** The line was interpreted successfully. */
  case object Success extends Result

  /** The line was erroneous in some way. */
  case object Error extends Result

  /** The input was incomplete.  The caller should request more input.
   */
  case object Incomplete extends Result
}
