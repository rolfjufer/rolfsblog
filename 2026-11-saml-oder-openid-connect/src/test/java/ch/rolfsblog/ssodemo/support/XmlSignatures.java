package ch.rolfsblog.ssodemo.support;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** Signs one element of an XML document with the JDK, as the identity provider would. */
public final class XmlSignatures {

    private XmlSignatures() {
    }

    public static String sign(String xml, String elementId, KeyPairs.Signer signer) {
        try {
            DocumentBuilderFactory documents = DocumentBuilderFactory.newInstance();
            documents.setNamespaceAware(true);
            Document document = documents.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            Element element = elementWithId(document, elementId);
            element.setIdAttribute("ID", true);

            XMLSignatureFactory signatures = XMLSignatureFactory.getInstance("DOM");
            Reference reference = signatures.newReference("#" + elementId,
                    signatures.newDigestMethod(DigestMethod.SHA256, null),
                    List.of(
                            signatures.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null),
                            signatures.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec) null)),
                    null, null);
            SignedInfo signedInfo = signatures.newSignedInfo(
                    signatures.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
                    signatures.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                    List.of(reference));
            // The schema wants the signature right after the issuer, the first child.
            DOMSignContext context =
                    new DOMSignContext(signer.privateKey(), element, element.getFirstChild().getNextSibling());
            context.setDefaultNamespacePrefix("ds");
            signatures.newXMLSignature(signedInfo, null).sign(context);

            StringWriter signed = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(signed));
            return signed.toString();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static Element elementWithId(Document document, String id) {
        NodeList elements = document.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            if (elements.item(i) instanceof Element element && id.equals(element.getAttribute("ID"))) {
                return element;
            }
        }
        throw new IllegalArgumentException("No element with ID " + id);
    }
}
