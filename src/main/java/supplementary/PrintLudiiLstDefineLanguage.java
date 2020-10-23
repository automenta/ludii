// 
// Decompiled by Procyon v0.5.36
// 

package supplementary;

import grammar.Symbol;
import language.grammar.Grammar;
import main.StringRoutines;

import java.util.ArrayList;
import java.util.List;

public class PrintLudiiLstDefineLanguage
{
    public static void main(final String[] args) {
        final List<String> classNames = new ArrayList<>();
        final List<String> constNames = new ArrayList<>();
        for (final Symbol s : Grammar.grammar().symbols()) {
            if (s.type() == Symbol.SymbolType.Class) {
                classNames.add(StringRoutines.lowerCaseInitial(s.name()));
            }
            else {
                if (s.type() != Symbol.SymbolType.Constant) {
                    continue;
                }
                constNames.add(s.name());
            }
        }
        System.out.println("\\lstdefinelanguage{ludii}{");
        System.out.println("  keywords={" + StringRoutines.join(",", classNames) + "},");
        System.out.println("  basewidth  = {.6em,0.6em},");
        System.out.println("  keywordstyle=\\color{mblue}\\bfseries,");
        System.out.println("  ndkeywords={" + StringRoutines.join(",", constNames) + "},");
        System.out.println("  ndkeywordstyle=\\color{dviolet}\\bfseries,");
        System.out.println("  identifierstyle=\\color{black},");
        System.out.println("  sensitive=true,   % need case-sensitivity for different keywords");
        System.out.println("  comment=[l]{//},");
        System.out.println("  commentstyle=\\color{dred}\\ttfamily,");
        System.out.println("  stringstyle=\\color{dgreen}\\ttfamily,");
        System.out.println("  morestring=[b]',");
        System.out.println("  morestring=[b]\",");
        System.out.println("  escapechar=@,");
        System.out.println("  showstringspaces=false,");
        System.out.println("  xleftmargin=1pt,xrightmargin=1pt,");
        System.out.println("  breaklines=true,basicstyle=\\ttfamily\\small,backgroundcolor=\\color{colorex},inputencoding=utf8/latin9,texcl");
        System.out.println("}");
    }
}
