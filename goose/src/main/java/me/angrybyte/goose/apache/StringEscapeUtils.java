/*
 * Copyright 2002-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.angrybyte.goose.apache;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Escapes and unescapes <code>String</code>s for Java, Java Script, HTML, XML, and SQL.
 * </p>
 *
 * @author Apache Jakarta Turbine
 * @author GenerationJavaCore library
 * @author Purple Technology
 * @author <a href="mailto:bayard@generationjava.com">Henri Yandell</a>
 * @author <a href="mailto:alex@purpletech.com">Alexander Day Chaffee</a>
 * @author Antony Riley
 * @author Helge Tesgaard
 * @author <a href="sean@boohai.com">Sean Brown</a>
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @author Phil Steitz
 * @author Pete Gieser
 * @version $Id: StringEscapeUtils.java 165657 2005-05-02 18:31:49Z ggregory $
 * @since 2.0
 */
public class StringEscapeUtils {

    /**
     * <p>
     * <code>StringEscapeUtils</code> instances should NOT be constructed in standard programming.
     * </p>
     * <p/>
     * <p>
     * Instead, the class should be used as:
     * 
     * <pre>
     * StringEscapeUtils.escapeJava(&quot;foo&quot;);
     * </pre>
     * 
     * </p>
     * <p>
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     * </p>
     */
    public StringEscapeUtils() {
    }

    // Java and JavaScript
    //--------------------------------------------------------------------------

    /**
     * <p>
     * Escapes the characters in a <code>String</code> using Java String rules.
     * </p>
     * <p/>
     * <p>
     * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.)
     * </p>
     * <p/>
     * <p>
     * So a tab becomes the characters <code>'\\'</code> and <code>'t'</code>.
     * </p>
     * <p/>
     * <p>
     * The only difference between Java strings and JavaScript strings is that in JavaScript, a single quote must be escaped.
     * </p>
     * <p/>
     * <p>
     * Example:
     * 
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn't say, \"Stop!\"
     * </pre>
     * 
     * </p>
     *
     * @param str String to escape values in, may be null
     * @return String with escaped values, <code>null</code> if null string input
     */
    public static String escapeJava(String str) {
        return escapeJavaStyleString(str, false);
    }

    /**
     * <p>
     * Escapes the characters in a <code>String</code> using Java String rules to a <code>Writer</code>.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> string input has no effect.
     * </p>
     *
     * @param out Writer to write escaped string into
     * @param str String to escape values in, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException if error occurs on underlying Writer
     * @see #escapeJava(java.lang.String)
     */
    public static void escapeJava(Writer out, String str) throws IOException {
        escapeJavaStyleString(out, str, false);
    }

    /**
     * <p>
     * Escapes the characters in a <code>String</code> using JavaScript String rules.
     * </p>
     * <p>
     * Escapes any values it finds into their JavaScript String form. Deals correctly with quotes and control-chars (tab, backslash, cr, ff,
     * etc.)
     * </p>
     * <p/>
     * <p>
     * So a tab becomes the characters <code>'\\'</code> and <code>'t'</code>.
     * </p>
     * <p/>
     * <p>
     * The only difference between Java strings and JavaScript strings is that in JavaScript, a single quote must be escaped.
     * </p>
     * <p/>
     * <p>
     * Example:
     * 
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn\'t say, \"Stop!\"
     * </pre>
     * 
     * </p>
     *
     * @param str String to escape values in, may be null
     * @return String with escaped values, <code>null</code> if null string input
     */
    public static String escapeJavaScript(String str) {
        return escapeJavaStyleString(str, true);
    }

    /**
     * <p>
     * Escapes the characters in a <code>String</code> using JavaScript String rules to a <code>Writer</code>.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> string input has no effect.
     * </p>
     *
     * @param out Writer to write escaped string into
     * @param str String to escape values in, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException if error occurs on underlying Writer
     * @see #escapeJavaScript(java.lang.String)
     **/
    public static void escapeJavaScript(Writer out, String str) throws IOException {
        escapeJavaStyleString(out, str, true);
    }

    private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes) {
        if (str == null) {
            return null;
        }
        try {
            StringPrintWriter writer = new StringPrintWriter(str.length() * 2);
            escapeJavaStyleString(writer, str, escapeSingleQuotes);
            return writer.getString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz;
        sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                out.write("\\u" + hex(ch));
            } else if (ch > 0xff) {
                out.write("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                out.write("\\u00" + hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b':
                        out.write('\\');
                        out.write('b');
                        break;
                    case '\n':
                        out.write('\\');
                        out.write('n');
                        break;
                    case '\t':
                        out.write('\\');
                        out.write('t');
                        break;
                    case '\f':
                        out.write('\\');
                        out.write('f');
                        break;
                    case '\r':
                        out.write('\\');
                        out.write('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            out.write("\\u00" + hex(ch));
                        } else {
                            out.write("\\u000" + hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        if (escapeSingleQuote) {
                            out.write('\\');
                        }
                        out.write('\'');
                        break;
                    case '"':
                        out.write('\\');
                        out.write('"');
                        break;
                    case '\\':
                        out.write('\\');
                        out.write('\\');
                        break;
                    default:
                        out.write(ch);
                        break;
                }
            }
        }
    }

    /**
     * <p>
     * Returns an upper case hexadecimal <code>String</code> for the given character.
     * </p>
     *
     * @param ch The character to convert.
     * @return An upper case hexadecimal <code>String</code>
     */
    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    /**
     * <p>
     * Unescapes any Java literals found in the <code>String</code>. For example, it will turn a sequence of <code>'\'</code> and
     * <code>'n'</code> into a newline character, unless the <code>'\'</code> is preceded by another <code>'\'</code>.
     * </p>
     *
     * @param str the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     */
    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringPrintWriter writer = new StringPrintWriter(str.length());
            unescapeJava(writer, str);
            return writer.getString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * <p>
     * Unescapes any Java literals found in the <code>String</code> to a <code>Writer</code>.
     * </p>
     * <p/>
     * <p>
     * For example, it will turn a sequence of <code>'\'</code> and <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> string input has no effect.
     * </p>
     *
     * @param out the <code>Writer</code> used to output unescaped characters
     * @param str the <code>String</code> to unescape, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException if error occurs on underlying Writer
     */
    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        out.write('\\');
                        break;
                    case '\'':
                        out.write('\'');
                        break;
                    case '\"':
                        out.write('"');
                        break;
                    case 'r':
                        out.write('\r');
                        break;
                    case 'f':
                        out.write('\f');
                        break;
                    case 't':
                        out.write('\t');
                        break;
                    case 'n':
                        out.write('\n');
                        break;
                    case 'b':
                        out.write('\b');
                        break;
                    case 'u': {
                        // uh-oh, we're in unicode country....
                        inUnicode = true;
                        break;
                    }
                    default:
                        out.write(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write('\\');
        }
    }

    /**
     * <p>
     * Unescapes any JavaScript literals found in the <code>String</code>.
     * </p>
     * <p/>
     * <p>
     * For example, it will turn a sequence of <code>'\'</code> and <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.
     * </p>
     *
     * @param str the <code>String</code> to unescape, may be null
     * @return A new unescaped <code>String</code>, <code>null</code> if null string input
     * @see #unescapeJava(String)
     */
    public static String unescapeJavaScript(String str) {
        return unescapeJava(str);
    }

    /**
     * <p>
     * Unescapes any JavaScript literals found in the <code>String</code> to a <code>Writer</code>.
     * </p>
     * <p/>
     * <p>
     * For example, it will turn a sequence of <code>'\'</code> and <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.
     * </p>
     * <p/>
     * <p>
     * A <code>null</code> string input has no effect.
     * </p>
     *
     * @param out the <code>Writer</code> used to output unescaped characters
     * @param str the <code>String</code> to unescape, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException if error occurs on underlying Writer
     * @see #unescapeJava(Writer, String)
     */
    public static void unescapeJavaScript(Writer out, String str) throws IOException {
        unescapeJava(out, str);
    }

    // HTML and XML
    //--------------------------------------------------------------------------

    /**
     * <p>
     * Escapes the characters in a <code>String</code> using HTML entities.
     * </p>
     * <p/>
     * <p>
     * For example:
     * </p>
     * <p>
     * <code>"bread" & "butter"</code>
     * </p>
     * becomes:
     * <p>
     * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
     * </p>
     * <p/>
     * <p>
     * Supports all known HTML 4.0 entities, including funky accents.
     * </p>
     *
     * @param str the <code>String</code> to escape, may be null
     * @return a new escaped <code>String</code>, <code>null</code> if null string input
     * @see #unescapeHtml(String)
     * @see </br><a href="http://hotwired.lycos.com/webmonkey/reference/special_characters/">ISO Entities</a>
     * @see </br><a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
     * @see </br><a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
     * @see </br><a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
     * @see </br><a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
     **/
    public static String escapeHtml(String str) {
        if (str == null) {
            return null;
        }
        //todo: add a version that takes a Writer
        //todo: rewrite underlying method to use a Writer instead of a StringBuffer
        return Entities.HTML40.escape(str);
    }

    /**
     * <p>
     * Unescapes a string containing entity escapes to a string containing the actual Unicode characters corresponding to the escapes.
     * Supports HTML 4.0 entities.
     * </p>
     * <p/>
     * <p>
     * For example, the string "&amp;lt;Fran&amp;ccedil;ais&amp;gt;" will become "&lt;Fran&ccedil;ais&gt;"
     * </p>
     * <p/>
     * <p>
     * If an entity is unrecognized, it is left alone, and inserted verbatim into the result string. e.g. "&amp;gt;&amp;zzzz;x" will become
     * "&gt;&amp;zzzz;x".
     * </p>
     *
     * @param str the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     * @see #escapeHtml(String)
     **/
    public static String unescapeHtml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.HTML40.unescape(str);
    }

    /**
     * <p>
     * Escapes the characters in a <code>String</code> using XML entities.
     * </p>
     * <p/>
     * <p>
     * For example: <tt>"bread" & "butter"</tt> => <tt>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</tt>.
     * </p>
     * <p/>
     * <p>
     * Supports only the five basic XML entities (gt, lt, quot, amp, apos). Does not support DTDs or external entities.
     * </p>
     *
     * @param str the <code>String</code> to escape, may be null
     * @return a new escaped <code>String</code>, <code>null</code> if null string input
     * @see #unescapeXml(java.lang.String)
     **/
    public static String escapeXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.escape(str);
    }

    /**
     * <p>
     * Unescapes a string containing XML entity escapes to a string containing the actual Unicode characters corresponding to the escapes.
     * </p>
     * <p/>
     * <p>
     * Supports only the five basic XML entities (gt, lt, quot, amp, apos). Does not support DTDs or external entities.
     * </p>
     *
     * @param str the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     * @see #escapeXml(String)
     **/
    public static String unescapeXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.unescape(str);
    }

    /**
     * <p>
     * Escapes the characters in a <code>String</code> to be suitable to pass to an SQL query.
     * </p>
     * <p/>
     * <p>
     * For example,
     * 
     * <pre>
     * statement.executeQuery(&quot;SELECT * FROM MOVIES WHERE TITLE='&quot; + StringEscapeUtils.escapeSql(&quot;McHale's Navy&quot;) + &quot;'&quot;);
     * </pre>
     * 
     * </p>
     * <p/>
     * <p>
     * At present, this method only turns single-quotes into doubled single-quotes (<code>"McHale's Navy"</code> =>
     * <code>"McHale''s Navy"</code>). It does not handle the cases of percent (%) or underscore (_) for use in LIKE clauses.
     * </p>
     * <p/>
     * see http://www.jguru.com/faq/view.jsp?EID=8881
     *
     * @param str the string to escape, may be null
     * @return a new String, escaped for SQL, <code>null</code> if null string input
     */
    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.replace(str, "'", "''");
    }

}
