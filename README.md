# rejava
taken as-is from http://rejava.sourceforge.net/ (2016-09-19)

(taken from original project page)

The reJ project aims to allow improved visibility into Java class files, whether they were created compiling from Java sources, another language or by any other kind of tool. Basically anything that will run in a Java Virtual Machine.

reJ is a library and a graphical tool for inspection (viewing, searching and comparing) and manipulation (modification, obfuscation, refactoring of methods, fields, attributes and code) of classfiles for the Java platform.

There are various robust libraries/APIs available for bytecode manipulation, such as:

BCEL - http://jakarta.apache.org/bcel/
ASM - http://asm.objectweb.org/
Serp - http://serp.sourceforge.net/
Each library has a slightly different focus. reJ uses it's own API which is focused to serve the needs of the user interface. The user interface with syntax coloring, search, compare and editing capabilities is something that is less readily available elsewhere.

---

The project also included a very nice tutorial but in flash. You can find that in the /tutorial directory.

**TL;DR**, yes you can use this tool to edit .class files. You do not even need to extract that .class file from a .jar file, rejava gladly open class files from a jar. The included tutorial is very much needed to understand how to use. But as a quick note, you should find the desired class to edit, then click on View -> check desired, e.g. Constant Pool

--

This is not my project but Sami Koivu. See is the [original license](https://github.com/thirdy/rejava/blob/master/license.readme).

