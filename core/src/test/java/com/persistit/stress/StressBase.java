/**
 * END USER LICENSE AGREEMENT (“EULA”)
 *
 * READ THIS AGREEMENT CAREFULLY (date: 9/13/2011):
 * http://www.akiban.com/licensing/20110913
 *
 * BY INSTALLING OR USING ALL OR ANY PORTION OF THE SOFTWARE, YOU ARE ACCEPTING
 * ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. YOU AGREE THAT THIS
 * AGREEMENT IS ENFORCEABLE LIKE ANY WRITTEN AGREEMENT SIGNED BY YOU.
 *
 * IF YOU HAVE PAID A LICENSE FEE FOR USE OF THE SOFTWARE AND DO NOT AGREE TO
 * THESE TERMS, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND PROVIDED YOU (A) DO
 * NOT USE THE SOFTWARE AND (B) RETURN THE SOFTWARE WITHIN THIRTY (30) DAYS OF
 * YOUR INITIAL PURCHASE.
 *
 * IF YOU WISH TO USE THE SOFTWARE AS AN EMPLOYEE, CONTRACTOR, OR AGENT OF A
 * CORPORATION, PARTNERSHIP OR SIMILAR ENTITY, THEN YOU MUST BE AUTHORIZED TO SIGN
 * FOR AND BIND THE ENTITY IN ORDER TO ACCEPT THE TERMS OF THIS AGREEMENT. THE
 * LICENSES GRANTED UNDER THIS AGREEMENT ARE EXPRESSLY CONDITIONED UPON ACCEPTANCE
 * BY SUCH AUTHORIZED PERSONNEL.
 *
 * IF YOU HAVE ENTERED INTO A SEPARATE WRITTEN LICENSE AGREEMENT WITH AKIBAN FOR
 * USE OF THE SOFTWARE, THE TERMS AND CONDITIONS OF SUCH OTHER AGREEMENT SHALL
 * PREVAIL OVER ANY CONFLICTING TERMS OR CONDITIONS IN THIS AGREEMENT.
 */

package com.persistit.stress;

import java.util.Random;

import com.persistit.Exchange;
import com.persistit.Value;
import com.persistit.test.AbstractTestRunnerItem;
import com.persistit.test.TestResult;
import com.persistit.util.ArgParser;

public abstract class StressBase extends AbstractTestRunnerItem {

    protected ArgParser _ap;

    protected StringBuilder _sb = new StringBuilder(20);
    protected StringBuilder _sb1 = new StringBuilder(4096);
    protected StringBuilder _sb2 = new StringBuilder(4096);

    protected Exchange _ex;
    protected Exchange _exs;
    protected String _rootName = "test";

    protected int _debugCount;
    protected int _repeatTotal;
    protected int _repeat;

    protected int _total;
    protected int _count;
    protected String _phase = "";

    protected Random _random = new Random();

    @Override
    public void tearDown() {
        _ex = null;
        _exs = null;
        _sb = null;
        _sb1 = null;
        _sb2 = null;
        _random = null;
    }

    @Override
    public double getProgress() {
        return 0.5;
    }

    @Override
    public String getProgressString() {
        return _repeat + "/" + _repeatTotal + " " + _phase + ":" + _count + "/" + _total;
    }

    protected void setPhase(final String phase) {
        verbose(phase);
        _phase = phase;
    }

    protected String getPhase() {
        return _phase;
    }

    protected synchronized void handleThrowable(final Throwable thx) {
        _result = new TestResult(false, thx);
        println(_result);
        printStackTrace(thx);
        forceStop();
    }

    protected void seed(final long seedValue) {
        _random.setSeed(seedValue);
    }

    protected int random(final int min, final int max) {
        return _random.nextInt(max - min) + min;
    }

    protected void setupTestValue(final Exchange ex, final int counter, final int length) {
        _sb1.setLength(0);
        _sb1.append(_rootName);
        _sb1.append(" length=");
        _sb1.append(length);
        _sb1.append(" ");

        final int more = length - _sb1.length();
        for (int i = 0; i < more; i++) {
            _sb1.append(' ');
        }
        fillLong(counter, 0, length, false);
        ex.getValue().putString(_sb1);
    }

    protected void compareValues(final Value value1, final Value value2) {
        boolean ok = true;
        if (!value2.isDefined() || value2.isNull()) {
            _result = new TestResult(false, "value not expected to be null");
            ok = false;
        } else if (!value1.equals(value2)) {
            final String difference = displayDifference(value1.getString(), value2.getString());
            _result = new TestResult(false, "Values differ: " + difference);
            ok = false;
        }
        if (!ok) {
            forceStop();
        }
    }

    protected void compareStrings(final String value1, final String value2) {
        boolean ok = true;
        if (value2 == null) {
            _result = new TestResult(false, "value not expected to be null");
            ok = false;
        } else if (!value2.equals(value1)) {
            final String difference = displayDifference(value1, value2);
            _result = new TestResult(false, "Values differ: " + difference);
            ok = false;
        }
        if (!ok) {
            forceStop();
        }
    }

    protected String displayDifference(final String s1, final String s2) {
        if (s1 == null) {
            return "\r\n  value1=null" + "\r\n  value2=\"" + (s2.length() < 100 ? s2 : summaryOfLongString(s2)) + "\"";
        }

        if ((s1.length() < 100) && (s2.length() < 100)) {
            return "\r\n  value1=\"" + s1 + "\"" + "\r\n  value2=\"" + s2 + "\"";
        } else if (s1.length() != s2.length()) {
            return "\r\n  value1=\"" + summaryOfLongString(s1) + "\"" + "\r\n  value2=\"" + summaryOfLongString(s2)
                    + "\"";

        }
        int p, q;
        for (p = 0; p < s1.length(); p++) {
            if (s1.charAt(p) != s2.charAt(p)) {
                break;
            }
        }
        for (q = s1.length(); --q >= 0;) {
            if (s1.charAt(q) != s2.charAt(q)) {
                break;
            }
        }

        return "\r\n  value1=\"" + summaryOfLongString(s1) + "\"" + "\r\n  value2=\"" + summaryOfLongString(s2) + "\""
                + " first,last differences at (" + p + "," + q + ")";

    }

    protected String summaryOfLongString(String s) {
        if (s.length() > 100) {
            s = s.substring(0, 100);
        }
        return "<length=" + s.length() + "> " + s;
    }

    protected void setupSimpleKeyString(final int counter, final int length) {
        fillLong(counter, 0, length, true);
    }

    protected void setupComplexKeyString(int v1, final int v2, final int length) {
        _sb1.setLength(length);
        int i;
        for (i = 0; i < length;) {
            _sb1.setCharAt(i, (char) (v1 % 10 + '0'));
            v1 /= 10;
            if (v1 == 0) {
                break;
            }
        }
        if (i < length) {
            _sb1.setCharAt(++i, '~');
        }
        fillLong(v2, i, length - i, true);
    }

    protected void fillLong(long value, final int offset, final int length, boolean leadZeroes) {
        if (_sb1.length() < offset + length) {
            _sb1.setLength(offset + length);
        }
        for (int i = offset + length; --i >= offset;) {
            _sb1.setCharAt(i, (char) (value % 10 + '0'));
            value /= 10;
            if ((value == 0) && !leadZeroes) {
                break;
            }
        }
    }

    protected int checksum(final Value value) {
        if (!value.isDefined()) {
            return -1;
        }
        return value.hashCode();
    }

    protected void dot() {
        if (_forceStop) {
            verboseln();
            verboseln("STOPPED");
            throw new RuntimeException("STOPPED");
        }
        if (isVerbose()) {
            final int i = _count;
            if ((i > 0) && (_dotGranularity > 0) && ((i % _dotGranularity) == 0)) {
                if ((i % (_dotGranularity * 10)) == 0) {
                    print(Integer.toString(i));
                } else {
                    print(".");
                }
            }
        }
    }
}
