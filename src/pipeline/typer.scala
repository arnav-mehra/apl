package pipeline.typer

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

import types.util.*
import types.data.*

import pipeline.parser.*

object Typer {
    def digest(caller: Node.Call, pt: ArrayBuffer[DataType] = ArrayBuffer()): Call = {
        if (!Calls.has(caller)) {
            val Node.Call(fn_name, bt) = caller
            val c = Call(fn_name, pt)
            Calls.add(caller, c)
            c.ret_type = Typer(caller).gen_ret_type
        }
        Calls.get(caller)
    }
}

class Typer(caller: Node.Call) {
    def call: Call = Calls.get(caller)
    def fn: Function = Functions.data(caller.f)

    def gen_ret_type: DataType = {
        gen_node_type(fn.bt)
    }

    def iter_nodes(bt: Nodes): Unit = {
        bt.foreach(gen_node_type)
    }

    def gen_node_type(bn: Node): DataType = {
        val tp: DataType = bn match {
            case Node.Fn(fn, ps, bt) => {
                DataType.Void // fn declaration doesnt return anything.
            }
            case bn_call: Node.Call => {
                val pt: ArrayBuffer[DataType] = bn_call.bt.map(gen_node_type)
                val c = Typer.digest(bn_call, pt)
                c.ret_type
            }
            case Node.Print(e) => {
                gen_node_type(e)
            }
            case Node.Const(n) => {
                n.get_type()
            }
            case Node.Get(v, fs_bt) if fs_bt.length == 0 => {
                call.get_var_type(v)
            }
            case Node.Get(v, fs_bt) => {
                iter_nodes(fs_bt)
                DataType.Any
            }
            case Node.Set(getter, op, e_bn) => {
                val et: DataType = gen_node_type(e_bn)
                if (op == "=" && getter.fs.length == 0) { // var assignment 
                    call.add_var_type(getter.v, et)
                }
                et
            }
            case Node.BinOp(e1_bn, op, e2_bn) => {
                val e1t: DataType = gen_node_type(e1_bn)
                val e2t: DataType = gen_node_type(e2_bn)
                e1t.binOp(op, e2t)
            }
            case Node.Match(c_bn, ls) => {
                val c = gen_node_type(c_bn)
                ls.map(_._1).foreach(gen_node_type)
                ls.map(_._2).foreach(gen_node_type)
                DataType.Any
            }
            case Node.While(c_bn, block) => {
                val c = gen_node_type(c_bn)
                gen_node_type(block)
                DataType.Void
            }
            case Node.ForIn(v, e_bn, block) => {
                call.add_var_type(v, DataType.Any)
                val e = gen_node_type(e_bn)
                if (!e.is_iterable) println("Cannot iterate over type " + e.str)
                gen_node_type(block)
                DataType.Void
            }
            case Node.ForTo(v, e1_bn, e2_bn, block) => {
                call.add_var_type(v, DataType.Number)
                val e1 = gen_node_type(e1_bn)
                val e2 = gen_node_type(e2_bn)
                gen_node_type(block)
                DataType.Void
            }
            case Node.Block(lines) => {
                val (bt_ls, bt_ret) = lines.splitAt(lines.length - 1)
                iter_nodes(bt_ls)
                gen_node_type(bt_ret(0))
            }
        }

        call.set_node_type(bn, tp)
        tp
    }   
}