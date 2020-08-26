
/*============================================================================

This Chisel source file is part of a pre-release version of the HardFloat IEEE
Floating-Point Arithmetic Package, by John R. Hauser (with some contributions
from Yunsup Lee and Andrew Waterman, mainly concerning testing).

Copyright 2010, 2011, 2012, 2013, 2014, 2015, 2016 The Regents of the
University of California.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
    this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions, and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. Neither the name of the University nor the names of its contributors may
    be used to endorse or promote products derived from this software without
    specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS", AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, ARE
DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

=============================================================================*/

package hardfloat.test

import hardfloat._
import chisel3._

class ValExec_fNFromRecFN(expWidth: Int, sigWidth: Int) extends Module
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val out = Output(Bits((expWidth + sigWidth).W))
        val check = Output(Bool())
        val pass = Output(Bool())
    })

    io.out :=
        fNFromRecFN(expWidth, sigWidth, recFNFromFN(expWidth, sigWidth, io.a))

    io.check := true.B
    io.pass := (io.out === io.a)
}

class FnFromRecFnFMASpec extends FMATester {
    def test(f: Int): Seq[String] = {
        test(
            s"f${f}FromRecF${f}",
            () => new ValExec_fNFromRecFN(exp(f), sig(f)),
            "fNFromRecFN.cpp",
            Seq(Seq("-level2", s"-f${f}"))
        )
    }

    "f16FromRecF16" should "pass" in {
        check(test(16))
    }

    "f32FromRecF32" should "pass" in {
        check(test(32))
    }

    "f64FromRecF64" should "pass" in {
        check(test(64))
    }
}

class FnFromRecFnMiterSpec extends MiterTester {
    def test(f: Int): Int = {
        generate(
            s"f${f}FromRecF${f}",
            () => new ValExec_fNFromRecFN(exp(f), sig(f))
        )
    }

    "f16FromRecF16" should "pass" in {
        check(test(16))
    }

    "f32FromRecF32" should "pass" in {
        check(test(32))
    }

    "f64FromRecF64" should "pass" in {
        check(test(64))
    }
}
