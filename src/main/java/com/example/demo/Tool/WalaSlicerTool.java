package com.example.demo.Tool;

import com.example.demo.Resources.SlicerType;
import com.example.demo.Resources.StatementInfo;
import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.modref.ModRef;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphSlicer;
import com.ibm.wala.util.strings.Atom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class WalaSlicerTool {
    private static final String EXCLUSIONS =
            "java\\/awt\\/.*\n"
                    + "javax\\/swing\\/.*\n"
                    + "sun\\/awt\\/.*\n"
                    + "sun\\/swing\\/.*\n"
                    + "com\\/sun\\/.*\n"
                    + "sun\\/.*\n"
                    + "org\\/netbeans\\/.*\n"
                    + "org\\/openide\\/.*\n"
                    + "com\\/ibm\\/crypto\\/.*\n"
                    + "com\\/ibm\\/security\\/.*\n"
                    + "org\\/apache\\/xerces\\/.*\n"
                    + "java\\/security\\/.*\n"
                    + "";
    public static List<String> methods = new ArrayList<>();
    public static Set<Integer> doSlicing(String appJar , String methodName , String className , int lineNumber) throws WalaException, CancelException, IOException, InvalidClassFileException {
        System.out.println(appJar + " " + methodName + " " + className + " " + lineNumber);
        AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(appJar,null);
        scope.setExclusions(new FileOfClasses(new ByteArrayInputStream(EXCLUSIONS.getBytes("UTF-8"))));
        ClassHierarchy cha = ClassHierarchyFactory.make(scope);

        Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope, cha);
        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);

        com.ibm.wala.ipa.callgraph.CallGraphBuilder cgb = Util.makeZeroCFABuilder(Language.JAVA ,options, new AnalysisCacheImpl(),cha, scope, null, null);

        //少一个参数
        CallGraph cg = cgb.makeCallGraph(options , null);

        PointerAnalysis pa = cgb.getPointerAnalysis();

        StatementInfo statementInfo = findSeedStatementInfoByLineNumber(findMainMethod(cg , appJar.split("\\\\")[appJar.split("\\\\").length - 1].split("\\.")[0] ,methodName , className), lineNumber);
        Set<Integer> sourcelines = new HashSet<>();
        if (statementInfo == null) {
            sourcelines.add(lineNumber);
            return sourcelines;
        }
        Collection<Statement> slice;

        // context-sensitive traditional slice
        ModRef<InstanceKey> modRef = ModRef.make();
        SDG<?> sdg = new SDG<>(cg, pa, modRef, Slicer.DataDependenceOptions.REFLECTION, Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES, null);
        if (statementInfo.getSlicerType() == SlicerType.BackwardSlice) {
            slice = Slicer.computeBackwardSlice(statementInfo.getSeedStatement(), cg, pa, Slicer.DataDependenceOptions.NO_BASE_PTRS,
                    Slicer.ControlDependenceOptions.NONE);
        }
        else {
            slice = Slicer.computeForwardSlice(statementInfo.getSeedStatement(), cg, pa, Slicer.DataDependenceOptions.NO_BASE_PTRS,
                    Slicer.ControlDependenceOptions.NONE);
        }

        Predicate<Statement> filter = o -> slice.contains(o) && !o.toString().contains("Primordial") && o.getKind() == Statement.Kind.NORMAL;
        Graph<Statement> graph = GraphSlicer.prune(sdg, filter);
        findAllMethod(appJar , className);
        for (Statement s : graph)
            if (methods.contains(s.getNode().getMethod().getName().toString()))
                sourcelines.add(getStatementLineNumber(s));
        sourcelines.add(lineNumber);
        return sourcelines;
    }

    //find the method where the seed statement is included.
    public static CGNode findMainMethod(CallGraph cg , String appJarName , String methodName , String className) {
        Atom name = Atom.findOrCreateUnicodeAtom(methodName);
        for (Iterator<? extends CGNode> it = cg.iterator(); it.hasNext();) {
            CGNode n = it.next();
            if (n.getMethod().getName().equals(name)  && n.getMethod().getDeclaringClass().getName().toString().equals("L" + "testcases/" + appJarName + "/" + className)) {
                System.out.println(n.getMethod().getDeclaringClass().getName().toString() + " " + n.getMethod().getName().toString());
                return n;
            }
        }
//        Assertions.UNREACHABLE("failed to find " + methodName +" method");
        return null;
    }

    public static void findAllMethod(String appJar , String className) throws ClassHierarchyException, CallGraphBuilderCancelException, IOException {
        CallGraph cg = GenerateCallGraph(appJar);
        Iterator<? extends CGNode> it = cg.iterator();
        while (it.hasNext()) {
            CGNode n = it.next();
            if (n.getMethod().getDeclaringClass().getName().toString().equals("L" + "testcases/" + appJar.split("\\\\")[appJar.split("\\\\").length - 1].split("\\.")[0] + "/" + className)) {
                methods.add(n.getMethod().getName().toString());
            }
        }
    }

    public static StatementInfo findSeedStatementInfoByLineNumber(CGNode n, int LineNumber) throws InvalidClassFileException {
        if (n == null)
            return null;
        IR ir = n.getIR();
        for (int i = 0 ; i < ir.getInstructions().length ; i ++) {
//            System.out.println(IRIdexToLineNumber(ir , i));
            if (IRIdexToLineNumber(ir , i) == LineNumber && ir.getInstructions()[i] != null) {
                StatementInfo statementInfo = new StatementInfo();
                if (ir.getInstructions()[i].getNumberOfUses() < ir.getInstructions()[i].getNumberOfDefs())
                    statementInfo.setSlicerType(SlicerType.BackwardSlice);
                else
                    statementInfo.setSlicerType(SlicerType.ForwardSlice);
                statementInfo.setSeedStatement(new NormalStatement(n , i));
                return statementInfo;
            }
        }
        Assertions.UNREACHABLE("failed to find call to "  + " in " + n);
        return null;
    }
    //According to LineNumber , find the seed statement
    public static Statement findSeedStatementByLineNumber(CGNode n, int LineNumber) throws InvalidClassFileException {
        IR ir = n.getIR();
        for (int i = 0 ; i < ir.getInstructions().length ; i ++) {
//            System.out.println(IRIdexToLineNumber(ir , i));
            if (IRIdexToLineNumber(ir , i) == LineNumber && ir.getInstructions()[i] != null) {
                return new NormalStatement(n , i);
            }
        }
        Assertions.UNREACHABLE("failed to find call to "  + " in " + n);
        return null;
    }

    public static AnalysisScope GenerateAnalysisScope(String appJar) throws IOException {
        AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(appJar,null);
        scope.setExclusions(new FileOfClasses(new ByteArrayInputStream(EXCLUSIONS.getBytes("UTF-8"))));
        return scope;
    }

    public static boolean CanBeSeedStatement(CGNode n, int LineNumber) throws InvalidClassFileException {
        return findSeedStatementByLineNumber(n , LineNumber) == null ? false : true;
    }

    public static boolean CanBeSeedStatement(CallGraph cg , String appJarName , String methodName , String className , int LineNumber) throws InvalidClassFileException {
        return CanBeSeedStatement(findMainMethod(cg , appJarName , methodName , className) , LineNumber);
    }

    public static boolean CanBeSeedStatement(String appJar , String appJarName , String methodName , String className , int LineNumber) throws ClassHierarchyException, CallGraphBuilderCancelException, IOException, InvalidClassFileException {
        return CanBeSeedStatement(GenerateCallGraph(appJar) , appJarName ,methodName , className , LineNumber);
    }

    public static CallGraph GenerateCallGraph(AnalysisOptions options , String appJar) throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {
        return GenerateCallGraphBuilder(appJar).makeCallGraph(options , null);
    }

    public static CallGraph GenerateCallGraph(String appJar) throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {
        return GenerateCallGraph(GenerateAnalysisOptions(appJar) , appJar);
    }

    public static AnalysisOptions GenerateAnalysisOptions(String appJar) throws IOException, ClassHierarchyException {
        AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(appJar,null);
        scope.setExclusions(new FileOfClasses(new ByteArrayInputStream(EXCLUSIONS.getBytes("UTF-8"))));
        ClassHierarchy cha = ClassHierarchyFactory.make(scope);
        Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope, cha);
        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
        return options;
    }

    public static CallGraphBuilder GenerateCallGraphBuilder(String appJar) throws IOException, ClassHierarchyException {
        // create an analysis scope representing the appJar as a J2SE application
        AnalysisScope scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(appJar,null);
//        AnalysisScopeReader.readJavaScope(appJar , null , slicer.class.getClassLoader());
        scope.setExclusions(new FileOfClasses(new ByteArrayInputStream(EXCLUSIONS.getBytes("UTF-8"))));
        ClassHierarchy cha = ClassHierarchyFactory.make(scope);

        Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope, cha);
        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
        com.ibm.wala.ipa.callgraph.CallGraphBuilder cgb = Util.makeZeroCFABuilder(Language.JAVA ,options, new AnalysisCacheImpl(),cha, scope, null, null);
        return cgb;
    }

    //IRindex to LineNumber
    public static int IRIdexToLineNumber(IR ir , int instructionIndex) throws InvalidClassFileException {
        IBytecodeMethod method = (IBytecodeMethod)ir.getMethod();
        int bytecodeIndex = method.getBytecodeIndex(instructionIndex);
        int sourceLineNum = method.getLineNumber(bytecodeIndex);
        return sourceLineNum;
    }

    public static int getStatementLineNumber(Statement statement) throws InvalidClassFileException {
        NormalStatement n = (NormalStatement) statement;
        IMethod method = n.getNode().getMethod();
        return method.getSourcePosition(n.getInstructionIndex()).getFirstLine();
    }

    public static void dumpSlice(Collection<Statement> slice) {
        for (Statement s : slice) {
            System.out.println(StmtFormater.format(s));
        }
    }
    public static void main(String[] args) throws IOException, WalaException, CancelException, InvalidClassFileException {
//        findAllMethod("src\\main\\java\\CWE15_External_Control_of_System_or_Configuration_Setting.jar" , "CWE15_External_Control_of_System_or_Configuration_Setting__connect_tcp_02");

//        System.out.println(doSlicing("src\\main\\java\\CWE15_External_Control_of_System_or_Configuration_Setting.jar" , "goodG2B1" , "CWE15_External_Control_of_System_or_Configuration_Setting__connect_tcp_02" ,166));
        System.out.println(doSlicing("src\\main\\java\\CWE15_External_Control_of_System_or_Configuration_Setting.jar" , "badSink" , "CWE15_External_Control_of_System_or_Configuration_Setting__Environment_75b" , 42));
    }
}
