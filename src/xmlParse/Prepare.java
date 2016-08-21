package xmlParse;
import jdk.internal.util.xml.impl.Input;
import nc.bs.framework.aop.AspectManager;
import nc.bs.framework.aop.AspectMeta;
import nc.bs.framework.aop.PatternType;
import nc.bs.framework.core.*;
import nc.bs.framework.core.common.*;
import nc.bs.framework.instantiator.CtorInstantiator;
import nc.bs.framework.instantiator.ObjectFactoryInstantiator;
import nc.bs.framework.instantiator.StaticFactoryInstantiator;
import nc.bs.framework.loading.ModulePrivateClassLoader;
import nc.bs.framework.server.BusinessAppServer;
import nc.bs.framework.server.ComponentMetaImpl;
import nc.bs.framework.server.Module;
import nc.bs.framework.server.deploy.DeployUtil;
import nc.bs.framework.server.deploy.TempModuleAttribute;
import nc.bs.framework.util.Messages;
import org.granite.stax.ElementXMLStreamReader;
import org.granite.xv.*;
import org.granite.lang.util.PathPattern;
import org.granite.xv.namerule.MapperNameRule;
import org.granite.xv.visitor.*;

import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangqchf on 2016/8/11.
 */
public class Prepare {
    private PathPattern<Visitor> mvPP;
    private PathPattern<SavePointHandle> sphPP;

    //pre prepare
    Server server;
    BusinessAppServer appServer;
    private PathPattern<Visitor> visitorPP;
    private PathPattern<Visitor> visitorPP5x;
    //initialize

    public void initialize()
    {
        //appServer = BusinessAppServer.getInstance();
        //appServer.init(null);
        //server = appServer;
    }

    private InputStream getInputStream(File f) throws Exception {
        FileInputStream fin = new FileInputStream(f);
        BufferedInputStream bin = new BufferedInputStream(fin);
        return bin;
    }

    public Module newContainer(URL url, File md) {
        //ClassLoader loader = DeployUtil.newModuleClassLoader(appServer.getClassLoaderReposiotry(),appServer.getClassLoader(), md);

        Module module = new Module(null,null,null, null, url);

        parseModule(md,module);
        return null;
    }

    public Xvs newModuleXvs() {
        prepare();
        Xvs xos = new Xvs();
        xos.setVisitorPathPattern(mvPP);
        return xos;
    }

     public Module parseModule(File md, Module module) {
        String path = "D:\\working_software\\nchome65\\modules\\baseapp\\META-INF\\appbcmanage.upm";

        InputStream in = null;
        Xvs cmntXvs = newCmntXvs(true);
         cmntXvs.setId("D:\\working_software\\nchome65\\modules\\baseapp\\META-INF\\appbcmanage.upm");
        File file = new File(path);
        try {
            in = getInputStream(file);
        }catch (Exception e)
        {
            System.out.print("what");
        }
        cmntXvs.setId(path.toString());
        try {
            cmntXvs.parse(module, in);
        }catch (Exception e)
        {
            System.out.println();
        }
        return  module;
    }

    public Xvs newCmntXvs(boolean v6x) {
        prepare();
        Xvs xos = new Xvs();
        xos.setSphPathPattern(sphPP);

        if (v6x) {
            xos.setVisitorPathPattern(visitorPP);
        } else {
            xos.setVisitorPathPattern(visitorPP5x);
        }
        return xos;
    }

     public void prepare() {
        if (mvPP == null) {
            mvPP = new PathPattern<Visitor>();
            mvPP.add("module", new SetterVisitor(Option.ATTRIBUTE));
            mvPP.add("module/description", new SetterVisitor(Option.TEXT));
            mvPP.add("module/classloader/private", new VisitorSupport() {

                @Override
                public void startElement(XMLStreamReader r, Xvs s)
                        throws Exception {
                    int count = r.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        if (r.getAttributeLocalName(i).equals("childFirst")) {
                            if (r.getAttributeValue(i).equals("true")) {
                                GenericContainer<?> m = (GenericContainer<?>) s
                                        .peek(-1);
                                ClassLoader l = m.getClassLoader();
                                if (l instanceof ModulePrivateClassLoader) {
                                    ((ModulePrivateClassLoader) l)
                                            .setChildFirst(true);
                                } else if (l.getParent() instanceof ModulePrivateClassLoader) {
                                    ((ModulePrivateClassLoader) l.getParent())
                                            .setChildFirst(true);
                                }
                            }
                        }
                    }
                }

            });
            mvPP.freeze();
        }

        if (sphPP == null) {
            sphPP = new PathPattern<SavePointHandle>();
            sphPP.add("module/*/component", new SavePointHandle() {
                public void beforeUndo(SavePoint sp, Xvs s) {
                    StringBuffer sb = new StringBuffer();
                    int size = s.size();
                    if (size > 0 && s.peek(-1) instanceof GenericContainer<?>) {
                        GenericContainer<?> m = (GenericContainer<?>) s
                                .peek(-1);
                        sb.append("module=").append(m.getName());
                        for (int i = 0; i < size; i++) {
                            Object o = s.peek();
                            if (o instanceof AbstractMeta) {
                                AbstractMeta meta = (AbstractMeta) o;
                                if (meta.getName() != null) {
                                    sb.append(" component=").append(
                                            meta.getName());
                                }
                                break;
                            }
                        }
                    }

                    Throwable thr = sp.getThrowableSource().getThrowable();
                    if (thr instanceof InvocationTargetException) {
                        thr = thr.getCause();
                    }


                }

                public void afterUndo(SavePoint sp, Xvs s)
                        throws HandleException {

                }

            });

            sphPP.add("module/aops/aspect", new SavePointHandle() {
                public void beforeUndo(SavePoint sp, Xvs s) {
                    StringBuffer sb = new StringBuffer();
                    int size = s.size();
                    if (size > 0 && s.peek(-1) instanceof GenericContainer<?>) {
                        GenericContainer<?> m = (GenericContainer<?>) s
                                .peek(-1);
                        sb.append("module=").append(m.getName());
                    }

                    Throwable thr = sp.getThrowableSource().getThrowable();
                    if (thr instanceof InvocationTargetException) {
                        thr = thr.getCause();
                    }
                }

                public void afterUndo(SavePoint sp, Xvs s)
                        throws HandleException {

                }
            });

            sphPP.add("module/rest/resource", new SavePointHandle() {
                public void beforeUndo(SavePoint sp, Xvs s) {
                    StringBuffer sb = new StringBuffer();
                    int size = s.size();
                    if (size > 0 && s.peek(-1) instanceof GenericContainer<?>) {
                        GenericContainer<?> m = (GenericContainer<?>) s
                                .peek(-1);
                        sb.append("module=").append(m.getName());
                    }

                    Throwable thr = sp.getThrowableSource().getThrowable();
                    if (thr instanceof InvocationTargetException) {
                        thr = thr.getCause();
                    }
                }

                public void afterUndo(SavePoint sp, Xvs s)
                        throws HandleException {

                }
            });

            sphPP.freeze();
        }

        if (visitorPP == null) {
            visitorPP = prepare(true);
        }

        if (visitorPP5x == null) {
            visitorPP5x = prepare(false);
        }

    }

    private PathPattern<Visitor> prepare(boolean v6x) {

        PathPattern<Visitor> visitorPP = new PathPattern<Visitor>();
        if (v6x) {
            visitorPP.add("module", new CreatorVisitor(
                    TempModuleAttribute.class));
            visitorPP.add("module", new SetterVisitor());
        }

        visitorPP.add("module/rest/resource", new VisitorSupport() {
            public void startElement(XMLStreamReader r, Xvs s) throws Exception {
                String resourceClassName = "";
                String exInfo = "";
                for (int i = 0; i < r.getAttributeCount(); i++) {
                    if (r.getAttributeLocalName(i).equals("classname")) {
                        resourceClassName = r.getAttributeValue(i);
                    } else if (r.getAttributeLocalName(i).equals("exinfo")) {
                        exInfo = r.getAttributeValue(i);
                    }
                }
                GenericContainer<?> c = (GenericContainer<?>) s.peek(-1);// ::TODO
                // Class<?> clazz =
                // c.getClassLoader().loadClass(resourceClassName);//
                String moduleName = c.getName();
                try {
                    Class deployerClass = Class
                            .forName("uap.ws.rest.deploy.RestExtensionManager");
                    Object obj = deployerClass.newInstance();
                    Class[] classes = { String.class, String.class };
                    Method m = deployerClass.getMethod("processAtDeploy",
                            classes);
                    String[] args = { moduleName, resourceClassName };
                    m.invoke(obj, args);
                } catch (Exception e) {
                }
            }

            public void endElement(XMLStreamReader r, Xvs s) throws Exception {
            }

        });

        visitorPP.add("module/aops/aspect", new VisitorSupport() {
            public void startElement(XMLStreamReader r, Xvs s) throws Exception {
                String ac = null;
                String scope = null;
                PatternType pt = null;
                Map<String, String> attrMap = null;
                for (int i = 0; i < r.getAttributeCount(); i++) {
                    if (r.getAttributeLocalName(i).equals("class")) {
                        ac = r.getAttributeValue(i);
                    } else if (r.getAttributeLocalName(i).equals("component")) {
                        scope = r.getAttributeValue(i);
                    } else if (r.getAttributeLocalName(i).equals("patternType")) {
                        String patternType = r.getAttributeValue(i);
                        if ("regex".equals(patternType)) {
                            pt = PatternType.regex;
                        } else if ("method".equals(patternType)) {
                            pt = PatternType.method;
                        } else {
                            pt = PatternType.ant;
                        }
                    } else if (r.getAttributeLocalName(i).equals("compAttr")) {
                        String compAttr = r.getAttributeValue(i);
                    }
                }

                if (pt == null) {
                    pt = PatternType.ant;
                }

                if (ac != null) {
                    GenericContainer<?> c = (GenericContainer<?>) s.peek(-1);
                    Class<?> clazz = c.getClassLoader().loadClass(ac);
                    AspectMeta am = new AspectMeta(clazz);
                    if (scope != null) {
                        am.addComponent(scope, pt);
                    }
                    am.setDefaultPatternType(pt);
                    am.setCompAttrMap(attrMap);
                    s.push(am);
                    c.getExtension(AspectManager.class).addAspectMeta(am);
                }
            }

            public void endElement(XMLStreamReader r, Xvs s) throws Exception {

                if (s.peek() instanceof AspectMeta) {
                    s.pop();
                }

            }

        });

        visitorPP.add("module/aops/aspect/component", new VisitorSupport() {
            public void startElement(XMLStreamReader r, Xvs s) {
                PatternType pt = PatternType.ant;
                for (int i = 0; i < r.getAttributeCount(); i++) {
                    if (r.getAttributeLocalName(i).equals("patternType")) {
                        String patternType = r.getAttributeValue(i);
                        if ("regex".equals(patternType)) {
                            pt = PatternType.regex;
                        } else if ("method".equals(patternType)) {
                            pt = PatternType.method;
                        } else {
                            pt = PatternType.ant;
                        }
                        break;
                    }
                }
                s.push(pt);
            }

            public void endElement(XMLStreamReader r, Xvs s) throws Exception {
                PatternType pt = (PatternType) s.pop();
                if (s.peek() instanceof AspectMeta) {
                    AspectMeta am = (AspectMeta) s.peek();
                    am.addComponent(s.getText().trim(), pt);
                }
            }
        });

        visitorPP.add("module/public", new AddConstantVisitor(true));
        visitorPP.add("module/private", new AddConstantVisitor(false));

        visitorPP.add("module/*/component", new CreateWithPreVisitor(
                ComponentMetaImpl.class, -1));
        visitorPP.add("module/*/component", new WireCurrentVisitor("public"));
        MapperNameRule mnr = new MapperNameRule();
        mnr.alias("tx", "txAttribute");
        visitorPP.add("module/*/component", new SetterVisitor(Option.ATTRIBUTE,
                mnr));

        if (v6x) {
            visitorPP.add("module/*/component", new VisitorSupport() {
                @Override
                public void endElement(XMLStreamReader r, Xvs s)
                        throws Exception {
                    super.endElement(r, s);
                }

                @Override
                public void startElement(XMLStreamReader r, Xvs s)
                        throws Exception {
                    ComponentMetaImpl metaImpl = (ComponentMetaImpl) s.peek();
                    TempModuleAttribute tma = (TempModuleAttribute) s.peek(-2);
                    Container c = (Container) s.peek(-1);
                    if (tma.getName() != null) {
                        metaImpl.setEjbName(tma.getName());
                    } else {

                        metaImpl.setEjbName(c.getName());
                    }

                    if ((tma.getFramework() != null && tma.getFramework())
                            || (tma.getFramework() == null && c.isFramework())) {
                        String cluster = metaImpl.getCluster();
                        if (cluster == null
                                || ComponentMeta.NONE.equals(cluster)
                                || ComponentMeta.NORMAL
                                .equalsIgnoreCase(cluster)) {
                            metaImpl.setCluster(ComponentMeta.FRAMEWORK);
                        } else if (ComponentMeta.SP.equals(cluster)) {
                            metaImpl.setCluster(ComponentMeta.MASTER);
                        } else if (ComponentMeta.NOSP.equals(cluster)) {
                            metaImpl.setCluster(ComponentMeta.NOMASTER);
                        }
                    }
                }

            });
        }
        visitorPP.add("module/*/component", new RandomWireVisitor("register",
                true, false, -1, 0));

        visitorPP.add("module/*/component", new VisitorSupport() {
            public void endElement(XMLStreamReader r, Xvs s) throws Exception {
                AbstractMeta meta = (AbstractMeta) s.peek();
                for (ExtensionProcessor ep : meta.getExtensionProcessors()) {
                    ep.processAtDeployEnd(meta.getContainer(), meta);
                }
            }
        });
        visitorPP.add("module/*/component", new CallVisitor("validate", false));

        visitorPP.add("module/*/component/description", new SetterVisitor(
                Option.TEXT));

        visitorPP.add("module/*/component/extension", new VisitorSupport() {
            @Override
            public void endElement(XMLStreamReader r, Xvs s) throws Exception {
                ExtensionProcessor ep = (ExtensionProcessor) s.pop();
                AbstractMeta meta = (AbstractMeta) s.peek();
                meta.addExtensionProcessor(ep);
            }

            @Override
            public void startElement(XMLStreamReader r, Xvs s) throws Exception {
                int count = r.getAttributeCount();
                for (int i = 0; i < count; i++) {
                    if (r.getAttributeLocalName(i).equals("class")) {
                        GenericContainer<?> module = (GenericContainer<?>) s
                                .peek(-1);
                        try {
                            Class<?> clazz = module.getClassLoader().loadClass(
                                    r.getAttributeValue(i).trim());
                            if (ExtensionProcessor.class
                                    .isAssignableFrom(clazz)) {
                                ExtensionProcessor ep = (ExtensionProcessor) clazz
                                        .newInstance();
                                ep.processAtDeploy(
                                        (GenericContainer<?>) s.peek(-1),
                                        (Meta) s.peek(),
                                        new ElementXMLStreamReader(r, false));
                                s.push(ep);
                                return;
                            }
                        } catch (Throwable thr) {
                            break;
                        }
                    }
                }

                ExtensionProcessor ep = new PrintExtensionProcessor();
                ep.processAtDeploy((GenericContainer<?>) s.peek(-1),
                        (Meta) s.peek(), r);
                s.push(ep);

            }
        });

        visitorPP.add("module/*/component/interface", new VisitorSupport() {
            public void endElement(XMLStreamReader r, Xvs s) throws Exception {
                String txt = s.getText().trim();
                GenericContainer<?> module = (GenericContainer<?>) s.peek(-1);
                int endPos = txt.indexOf('@');
                Class<?> clazz = module.getClassLoader().loadClass(
                        endPos < 0 ? txt : txt.substring(0, endPos));
                AbstractMeta meta = (AbstractMeta) s.peek();

                meta.addInterface(clazz);

                if (meta.getName() == null || meta.isSupportAlias()) {
                    meta.addAlia(txt);
                }

            }
        });

        visitorPP.add("module/*/component/implementation",
                new VisitorSupport() {
                    public void endElement(XMLStreamReader r, Xvs s)
                            throws Exception {
                        String txt = s.getText().trim();
                        GenericContainer<?> module = (GenericContainer<?>) s
                                .peek(-1);

                        AbstractMeta meta = (AbstractMeta) s.peek();

                        Class<?> clazz = module.getClassLoader().loadClass(txt);
                        meta.setImplementation(clazz);
                        CtorInstantiator inst = new CtorInstantiator(clazz);

                        meta.setRawInstantiator(inst);

                    }
                });

        visitorPP.add("module/*/component/**/parameter|property/**/bean",
                new VisitorSupport() {
                    public void endElement(XMLStreamReader r, Xvs s)
                            throws Exception {
                        Object v = s.pop();
                        Object obj = s.peek();
                        if (v instanceof RudeRef) {
                            ((RudeRef) v).setRef(s.getText());
                        } else if (v instanceof RudeValue) {
                            ((RudeValue) v).setValue(s.getText());
                        }
                        if (obj instanceof FactoryParameter) {
                            ((FactoryParameter) obj).setValue(v);
                        } else if (obj instanceof RudeList) {
                            ((RudeList) obj).add(v);
                        } else if ((obj instanceof RudeSet)) {
                            ((RudeSet) obj).add(v);
                        } else if (obj instanceof Entry) {
                            ((Entry) obj).setObject(v);
                        } else if (obj instanceof PropertyHolder) {
                            ((PropertyHolder) obj).setValue(v);
                        }
                    }

                    public void startElement(XMLStreamReader r, Xvs s)
                            throws Exception {
                        RudeBean rudeBean = new RudeBean();
                        s.push(rudeBean);
                        String bcName = r.getAttributeValue(null, "class");
                        GenericContainer<?> module = (GenericContainer<?>) s
                                .peek(-1);
                        rudeBean.setContainer(module);
                        if (bcName != null) {
                            Class<?> clazz = module.getClassLoader().loadClass(
                                    bcName);
                            CtorInstantiator inst = new CtorInstantiator(clazz);
                            rudeBean.setRawInstantiator(inst);
                        }
                    }
                });

        visitorPP.add("module/*/component/**/construct", new CreatorVisitor(
                Arguments.class));

        visitorPP.add("module/*/component/**/construct", new WirePreVisitor(
                "rawInstantiator.parameters", false, false));

        visitorPP.add("module/*/component/**/parameter", new CreatorVisitor(
                FactoryParameter.class));
        visitorPP.add("module/*/component/**/parameter", new SetterVisitor(
                Option.ATTRIBUTE));
        visitorPP.add("module/*/component/**/parameter", new WirePreVisitor(
                "add", true, false));


        visitorPP.add("module/*/component/**/parameter|property/**/map/entry",
                new CreatorVisitor(Entry.class));
        visitorPP.add("module/*/component/**/parameter|property/**/map/entry",
                new SetterVisitor(Option.ATTRIBUTE));
        visitorPP.add("module/*/component/**/parameter|property/**/map/entry",
                new VisitorSupport() {
                    public void endElement(XMLStreamReader r, Xvs s) {
                        Entry entry = (Entry) s.peek();
                        RudeMap map = (RudeMap) s.peek(1);
                        map.put(entry.getKey(), entry.getObject());
                    }
                });

        ;

        visitorPP.add("module/*/component/**/parameter|property/**/props/prop",
                new VisitorSupport() {
                    public void startElement(XMLStreamReader r, Xvs s) {
                        Entry entry = new Entry();
                        int count = r.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String an = r.getAttributeLocalName(i);
                            if ("key".equals(an)) {
                                entry.setKey(r.getAttributeValue(i));
                            }
                        }

                        s.push(entry);
                    }

                    public void endElement(XMLStreamReader r, Xvs s) {
                        Entry entry = (Entry) s.pop();
                        entry.setObject(s.getText());
                        Properties props = (Properties) s.peek();
                        props.setProperty(entry.getKey(),
                                (String) entry.getObject());
                    }
                });

        visitorPP.add("module/*/component/**/property", new VisitorSupport() {
            public void startElement(XMLStreamReader r, Xvs s) {
                PropertyHolder property = new PropertyHolder();
                int count = r.getAttributeCount();
                for (int i = 0; i < count; i++) {
                    String an = r.getAttributeLocalName(i);
                    if ("name".equals(an)) {
                        property.setName(r.getAttributeValue(i));
                    }
                }
                s.push(property);
            }

            public void endElement(XMLStreamReader r, Xvs s) {
                PropertyHolder property = (PropertyHolder) s.pop();
                CreateInfoBag meta = (CreateInfoBag) s.peek();
                meta.addPropertyHolder(property);
            }
        });

        visitorPP.add("module/*/component/factory-method",
                new VisitorSupport() {
                    public void startElement(XMLStreamReader r, Xvs s) {
                        int count = r.getAttributeCount();
                        String mthdName = null;
                        for (int i = 0; i < count; i++) {
                            String an = r.getAttributeLocalName(i);
                            if ("method".equals(an)) {
                                mthdName = r.getAttributeValue(i);
                            }
                        }

                        FactoryDesc fm = new FactoryDesc();
                        fm.setMethodName(mthdName);
                        s.push(fm);
                        s.push(new Arguments<FactoryParameter>());
                    }

                    public void endElement(XMLStreamReader r, Xvs s) {
                        GenericContainer<?> module = (GenericContainer<?>) s
                                .peek(-1);
                        @SuppressWarnings("unchecked")
                        Arguments<FactoryParameter> params = (Arguments<FactoryParameter>) s
                                .pop();
                        FactoryDesc fd = (FactoryDesc) s.pop();
                        fd.setParameters(params
                                .toArray(new FactoryParameter[params.size()]));
                        if (fd.getObject() instanceof RudeValue) {
                            String cn = ((RudeValue) fd.getObject()).getValue();
                            cn = cn.trim();
                            try {
                                StaticFactoryInstantiator inst = new StaticFactoryInstantiator(
                                        module.getClassLoader().loadClass(cn),
                                        fd.getMethodName(), fd.getParameters());
                                AbstractMeta meta = (AbstractMeta) s.peek();
                                meta.setRawInstantiator(inst);
                            } catch (ClassNotFoundException e) {

                            }
                        } else {
                            ObjectFactoryInstantiator inst = new ObjectFactoryInstantiator(
                                    fd.getObject(), fd.getMethodName(), fd
                                    .getParameters());
                            AbstractMeta meta = (AbstractMeta) s.peek();
                            meta.setRawInstantiator(inst);
                        }
                    }

                });

        visitorPP.add("module/*/component/factory-method/provider/value",
                new VisitorSupport() {
                    public void endElement(XMLStreamReader r, Xvs s) {
                        RudeValue v = new RudeValue();

                        v.setValue(s.getText());

                        FactoryDesc fm = (FactoryDesc) s.peek(1);
                        fm.setObject(v);
                    }
                });

        visitorPP.add("module/*/component/factory-method/provider/ref",
                new VisitorSupport() {
                    public void endElement(XMLStreamReader r, Xvs s) {
                        RudeRef ref = new RudeRef();
                        ref.setRef(s.getText());
                        FactoryDesc fm = (FactoryDesc) s.peek(1);
                        fm.setObject(ref);
                    }
                });
        visitorPP.freeze();
        return visitorPP;

    }
}


