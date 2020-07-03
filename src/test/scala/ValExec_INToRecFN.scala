
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

class
    ValExec_UINToRecFN(intWidth: Int, expWidth: Int, sigWidth: Int)
    extends Module
{
    val io = IO(new Bundle {
        val in = Input(Bits(intWidth.W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))

        val expected = new Bundle {
            val out = Input(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Input(Bits(5.W))
            val recOut = Input(Bits((expWidth + sigWidth + 1).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((expWidth + sigWidth + 1).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val iNToRecFN = Module(new INToRecFN(intWidth, expWidth, sigWidth))
    iNToRecFN.io.signedIn := false.B
    iNToRecFN.io.in := io.in
    iNToRecFN.io.roundingMode   := io.roundingMode
    iNToRecFN.io.detectTininess := io.detectTininess

    io.expected.recOut := recFNFromFN(expWidth, sigWidth, io.expected.out)

    io.actual.out := iNToRecFN.io.out
    io.actual.exceptionFlags := iNToRecFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        equivRecFN(expWidth, sigWidth, io.actual.out, io.expected.recOut) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

class
    ValExec_INToRecFN(intWidth: Int, expWidth: Int, sigWidth: Int)
    extends Module
{
    val io = new Bundle {
        val in = Input(Bits(intWidth.W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = UInt(1.W)

        val expected = new Bundle {
            val out = Input(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Input(Bits(5.W))
            val recOut = Output(Bits((expWidth + sigWidth + 1).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((expWidth + sigWidth + 1).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    }

    val iNToRecFN = Module(new INToRecFN(intWidth, expWidth, sigWidth))
    iNToRecFN.io.signedIn := true.B
    iNToRecFN.io.in := io.in
    iNToRecFN.io.roundingMode   := io.roundingMode
    iNToRecFN.io.detectTininess := io.detectTininess

    io.expected.recOut := recFNFromFN(expWidth, sigWidth, io.expected.out)

    io.actual.out := iNToRecFN.io.out
    io.actual.exceptionFlags := iNToRecFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        equivRecFN(expWidth, sigWidth, io.actual.out, io.expected.recOut) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

class INToRecFNFMASpec extends FMATester {
    def test(i: Int, f: Int): Seq[String] = {
        test(
            s"I${i}ToRecF${f}",
            () => new ValExec_INToRecFN(i, exp(f), sig(f)),
            Seq("-level2", s"i${i}_to_f${f}")
        )
    }
    "I32ToRecF16" should "pass" in {
        check(test(32, 16))
    }
    "I32ToRecF32" should "pass" in {
        check(test(32, 32))
    }
    "I32ToRecF64" should "pass" in {
        check(test(32, 64))
    }
    "I64ToRecF16" should "pass" in {
        check(test(64, 16))
    }
    "I64ToRecF32" should "pass" in {
        check(test(64, 32))
    }
    "I64ToRecF64" should "pass" in {
        check(test(64, 64))
    }
}

class INToRecFNMiterSpec extends MiterTester {
    def test(i: Int, f: Int): Int = {
        generate(
            s"I${i}ToRecF${f}",
            () => new ValExec_INToRecFN(i, exp(f), sig(f))
        )
    }
    "I32ToRecF16" should "pass" in {
        check(test(32, 16))
    }
    "I32ToRecF32" should "pass" in {
        check(test(32, 32))
    }
    "I32ToRecF64" should "pass" in {
        check(test(32, 64))
    }
    "I64ToRecF16" should "pass" in {
        check(test(64, 16))
    }
    "I64ToRecF32" should "pass" in {
        check(test(64, 32))
    }
    "I64ToRecF64" should "pass" in {
        check(test(64, 64))
    }
}
