file:///C:/Users/Arnav/Documents/GitHub/apl/apl/index.scala
### java.lang.AssertionError: NoDenotation.owner

occurred in the presentation compiler.

action parameters:
offset: 1295
uri: file:///C:/Users/Arnav/Documents/GitHub/apl/apl/index.scala
text:
```scala
package utility

import scala.util.parsing.combinator.JavaTokenParsers
import scala.collection.mutable.HashMap

class DoubleObj(private var _v : Double) {
    def v = _v
    def v_=(v: Double) = {
        _v = v
    }
}

object ProgramParser extends JavaTokenParsers {
    def program:Parser[List[Any]] = rep(line) ^^ (lst => lst)

    def function_block:Parser[Any] = "fn " ~ variable ~ block ^^ {
        case "fn "~v~b => (v, b)
        case default => 0
    }

    def while_block:Parser[Any] = "while " ~ statement ~ block ^^ {
        case "while "~s~b => (s, b)
        case default => 0
    }

    def if_block:Parser[Any] = "if " ~ statement ~ block ^^ {
        case "if "~s~b => (s, b)
        case default => 0
    }

    def block:Parser[Any] = "{" ~ rep(line) ~ "}" ^^ {
        case "{"~lst~"}" => lst
        case default => 0
    }

    def line:Parser[Any] =
        statement ~ ";" ^^ {
            case s~";" => s
        }
 if_block
 while_block
 function_block

    def statement:Parser[String] = "[^;{}]*".r ^^ (s => s)

    def variable:Parser[String] = "[a-z]+".r ^^ (s => s)

    def run_iter(tree: List[Any]) = {
        for ln <- tree do {
            ln match {
                case List[]@@
            }
            if (ln typeof List[Any]) {
                run_iter(ln)
            } else {
                LineParser.parseAll(LineParser.line, ln)
            }
        }
    }

    def run(code: String) = {
        val min_code = code.replaceAll("\\s", "")
                           .replaceAll("if", "if ")
                           .replaceAll("while", "while ")
                           .replaceAll("fn", "fn ")
        val res = parseAll(program, min_code)
        run_iter(res.get)
    }

    def main(args: Array[String]) = {
        val code : String = """
            x = 10;
            while x {
                x -= 1;
                if x {
                    x += 1;
                }
            }
        """
        run(code)
    }
}

object LineParser extends JavaTokenParsers {
    val variables : HashMap[String, DoubleObj] = HashMap();

    def line:Parser[DoubleObj] = setter | form

    def setter:Parser[DoubleObj] = variable ~ ("="|"+="|"-="|"*="|"/=") ~ form ^^ {
        case x~"="~f =>  x.v = f.v;  x
        case x~"+="~f => x.v += f.v; x
        case x~"-="~f => x.v -= f.v; x
        case x~"*="~f => x.v *= f.v; x
        case x~"/="~f => x.v /= f.v; x
    }

    def form:Parser[DoubleObj] = term ~ rep(("+" | "-") ~ term) ^^ {
        case t1~lst => {
            new DoubleObj(
                lst.foldLeft(t1.v)(
                    (acc, t) => if (t._1 == "+") acc + t._2.v else acc - t._2.v
                )
            )
        }
    }
    def term:Parser[DoubleObj] = factor ~ rep(("*" | "/") ~ factor) ^^ {
        case f1~lst => {
            new DoubleObj(
                lst.foldLeft(f1.v)(
                    (acc, f) => if (f._1 == "*") acc * f._2.v else acc / f._2.v
                )
            )
        }
    }
    def factor:Parser[DoubleObj] =
        "(" ~ form ~ ")" ^^ (x => x._1._2)
 number
 variable

    // def array: Parser[List[Double]] = "[" ~ number ~ rep("," ~ number) ~ "]" ^^ {
    //     case "["~n~lst~"]" => {
    //         List(n) ::: lst.map(p => p._2)
    //     }
    // }
    def variable: Parser[DoubleObj]   = """[a-z]+""".r ^^ {
        s => {
            if (!variables.contains(s)) {
                variables(s) = new DoubleObj(0)   
            }
            variables(s)
        }
    }
    def number: Parser[DoubleObj] = """(0|[1-9]\d*)""".r ^^ {
        s => new DoubleObj(s.toDouble)
    }

    def run(code: String) = {
        code.replaceAll("\\s", "")
            .split(";")
            .filter(x => x.length() > 0)
            .foreach(l => {
                println(l)
                val res = parseAll(line, l)
                println(res.get.v)
            })
    }

    // def main(args: Array[String]) = {
    //     val code : String = """
    //         x = 1;
    //         y = 1;
    //         x *= 2;
    //         x *= 2;
    //         x;
    //     """

    //     // run(code)
        
    //     // 2+3*5 
    //     // => term(2) [+ term(3*5)]
    //     // => (factor(2), []) [+ (factor(3), [* factor(5)])]
    //     // => (float(2), []) [+ (float(3), [* float(5)])]
    // }
}
```



#### Error stacktrace:

```
dotty.tools.dotc.core.SymDenotations$NoDenotation$.owner(SymDenotations.scala:2582)
	scala.meta.internal.pc.SignatureHelpProvider$.isValid(SignatureHelpProvider.scala:83)
	scala.meta.internal.pc.SignatureHelpProvider$.notCurrentApply(SignatureHelpProvider.scala:96)
	scala.meta.internal.pc.SignatureHelpProvider$.$anonfun$1(SignatureHelpProvider.scala:48)
	scala.collection.StrictOptimizedLinearSeqOps.loop$3(LinearSeq.scala:280)
	scala.collection.StrictOptimizedLinearSeqOps.dropWhile(LinearSeq.scala:282)
	scala.collection.StrictOptimizedLinearSeqOps.dropWhile$(LinearSeq.scala:278)
	scala.collection.immutable.List.dropWhile(List.scala:79)
	scala.meta.internal.pc.SignatureHelpProvider$.signatureHelp(SignatureHelpProvider.scala:48)
	scala.meta.internal.pc.ScalaPresentationCompiler.signatureHelp$$anonfun$1(ScalaPresentationCompiler.scala:388)
```
#### Short summary: 

java.lang.AssertionError: NoDenotation.owner