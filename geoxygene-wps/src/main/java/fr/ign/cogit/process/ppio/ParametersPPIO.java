package fr.ign.cogit.process.ppio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.geoserver.wps.ppio.CDataPPIO;

import fr.ign.cogit.parameters.Parameters;

/**
 * 
 * @author Marie-Dominique Van Damme
 *
 */
public class ParametersPPIO extends CDataPPIO {
  
  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(ParametersPPIO.class.getName());
  
  /**
   * Default constructor.
   */
  protected ParametersPPIO() {
      super(Parameters.class, Parameters.class, "text/xml");
  }
  
  @Override
  public void encode(Object value, OutputStream os) throws IOException {
      throw new UnsupportedOperationException("Unsupported encode(Object, OutputStream) operation.");
  }
  
  @Override
  public Object decode(InputStream input) throws Exception {
      LOGGER.debug("====================================================================");
      LOGGER.debug("PPIO inputstream");
      LOGGER.debug(input);
      LOGGER.debug("====================================================================");
      throw new UnsupportedOperationException("Unsupported decode(InputStream) operation.");
  }
  
  @Override
  public Object decode(String inputXML) throws Exception {
      LOGGER.debug("====================================================================");
      LOGGER.debug("PPIO input");
      LOGGER.debug(inputXML);
      LOGGER.debug("====================================================================");
      return Parameters.unmarshall(inputXML);
  }
  
  
  @Override
  public String getFileExtension() {
      return "xml";
  }

}