/*
 * Copyright (c) 2014 ThingMagic, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.thingmagic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;


public class LoadSaveConfiguration
{

   public  List<String> booleanParameters;
   public  List<String> readOnlyParameters;
   boolean isRollback = true;
   Map<String, Class> enumClass;
    
    public void loadConfiguration(String filePath, Reader r) throws ReaderException
    {   
        String tempFilePath = "";
        File tempFile = null;
        String readerModel = "";
        try 
        {
            readerModel = r.paramGet("/reader/version/model").toString();
            Properties prop = new Properties();
            File configFile = new File(filePath);
            if (!configFile.exists()) 
            {
                throw new FileNotFoundException("Unable to find the configuration properties file in "+filePath);
            }
            SimpleDateFormat sdft = new SimpleDateFormat ("yyyy-MM-dd_HH-mm-ss");
            tempFilePath = configFile.getParent()+"\\DeviceConfig."+sdft.format(new Date())+ ".urac";
            tempFile = new File(tempFilePath);
            tempFile.createNewFile();
            FileInputStream fis = new FileInputStream(configFile);
            prop.load(fis);
            fis.close();
            if (booleanParameters == null && enumClass == null)
            {
                addbooleanParameters();
                addEnumClasses();
            }
            r.saveConfig(tempFilePath);
       
            Set<Object> keys = prop.keySet();
            for (Object key : keys) 
            {
                String keyParam = key.toString().trim();
                String value = prop.getProperty(keyParam).trim();
                if(!keyParam.startsWith("/reader"))
                {
                    System.out.println("\"" + keyParam + "\" is not a valid parameter ");
                    continue;
                }
               
                try
                {
                    if(!readOnlyParameters.contains(keyParam))
                    {
                        r.paramSet(keyParam,  parseValue(keyParam, value)); 
                    }
                }
                catch(Exception ex)
                {
                    if(ex instanceof IllegalArgumentException || ex instanceof ReaderCodeException)
                    {
                        String message = ex.getMessage();
                        if( message.contains("No parameter named")
                           || message.contains("Parameter '" + key + "' is read-only.") 
                           || message.contains("Parameter is read only."))
                        {                            
                            r.notifyExceptionListeners(new ReaderException(keyParam + " is either read only or not supported by reader. Skipping this param"));
                        }
                        else if(message.contains("Wrong type"))
                        {
                           r.notifyExceptionListeners(new ReaderException("Wrong type  "+ value + " for "+ keyParam+". Skipping this param"));
                        }
                        else if ((readerModel.contains("M6e Micro") || readerModel.contains("M6e Nano"))
                                && ex.getMessage().contains("Invalid antenna"))
                        {
                            r.notifyExceptionListeners(new ReaderException("Invalid value " + value + " for " + keyParam+". Skipping this param"));
                        }
                       else if(ex.getMessage().contains("Unimplemented feature"))
                       {
                          r.notifyExceptionListeners(new ReaderException("Feature not supported for "+ keyParam+". Skipping this param"));
                       }
                       else if(ex.getMessage().contains("Illegal set of GPI for trigger read"))
                       {
                          r.notifyExceptionListeners(new ReaderException("Invalid value " + value + " for " + keyParam +" "+ex.getMessage()+". Skipping this param"));
                       }
                       else if(ex.getMessage().contains("The reader received a valid command with an unsupported or invalid parameter"))
                       {
                          r.notifyExceptionListeners(new ReaderException("The reader received a valid command with an unsupported or "
                                  + "invalid parameter value for " + keyParam +". Skipping this param"));
                       }
                       else
                        {
                            if (isRollback)
                            {
                                isRollback = false;
                                r.notifyExceptionListeners(new ReaderException("Invalid value " + value
                                        + " for " + keyParam + " " + ex.getMessage()));
                                rollBackConfigData(r, tempFilePath);
                                tempFile.delete();
                                break;
                            }
                            r.notifyExceptionListeners(new ReaderException("Invalid value " + value + " for " + keyParam + " " + ex.getMessage()));
                        }
                    }
                    else if(ex instanceof UnsupportedOperationException)
                    {
                        r.notifyExceptionListeners(new ReaderException("Feature not supported for "+ keyParam+". Skipping this param"));
                    }
                    else if(ex instanceof ReaderCommException)
                       {
                           if (-1 != ex.getMessage().indexOf("Timeout"))
                            {
                                  throw new ReaderException(ex.getMessage());
                            }
                       }
                    else
                    {
                        if(isRollback)
                        {
                            isRollback = false;
                            r.notifyExceptionListeners(new ReaderException("Invalid value " + value +
                                    " for " + keyParam + " " + ex.getMessage()));
                            rollBackConfigData(r, tempFilePath);
                            tempFile.delete();
                            break;
                        }
                        r.notifyExceptionListeners(new ReaderException("Invalid value " + value +" for " + keyParam+ " " +ex.getMessage()));
                    }
                }
            }
            tempFile.delete();
        }
        catch (Exception ex)
        {
            if(tempFile != null)
            {
               tempFile.delete();
            }
            throw new ReaderException(ex.getMessage());
        }
    }
   
    public void rollBackConfigData(Reader r, String filePath) throws ReaderException, IOException
    {
        r.notifyExceptionListeners(new ReaderException("Rolling back the configuration data"));
        loadConfiguration(filePath, r);
    }
    
    public void addbooleanParameters()
    {
        booleanParameters = new ArrayList();
        booleanParameters.add("/reader/region/lbt/enable");
        booleanParameters.add("/reader/antenna/checkport");
        booleanParameters.add("/reader/tagreaddata/recordhighestrssi");
        booleanParameters.add("/reader/tagreaddata/uniquebyantenna");
        booleanParameters.add("/reader/tagreaddata/uniquebydata");
        booleanParameters.add("/reader/tagreaddata/reportrssiindbm");
        booleanParameters.add("/reader/radio/enablepowersave");
        booleanParameters.add("/reader/status/antennaenable");
        booleanParameters.add("/reader/status/frequencyenable");
        booleanParameters.add("/reader/status/temperatureenable");
        booleanParameters.add("/reader/tagreaddata/reportrssiIndbm");
        booleanParameters.add("/reader/tagreaddata/uniquebyprotocol");
        booleanParameters.add("/reader/tagreaddata/enablereadfilter");
        booleanParameters.add("/reader/radio/enablesjc");
        booleanParameters.add("/reader/gen2/writeearlyexit");
        booleanParameters.add("/reader/extendedepc");
    }
    public void addEnumClasses()
    {
       enumClass  = new HashMap<String,Class>();
       enumClass.put("/reader/region/id", Reader.Region.class);
       enumClass.put("/reader/powermode", SerialReader.PowerMode.class);
       enumClass.put("/reader/tagop/protocol", TagProtocol.class);
       enumClass.put("/reader/gen2/session", Gen2.Session.class);
       enumClass.put("/reader/gen2/blf", Gen2.LinkFrequency.class);
       enumClass.put("/reader/gen2/tagencoding", Gen2.TagEncoding.class);
       enumClass.put("/reader/gen2/target", Gen2.Target.class);
       enumClass.put("/reader/gen2/tari", Gen2.Tari.class);
       enumClass.put("/reader/gen2/protocolextension", Gen2.ProtocolExtension.class);
       
    }
    
    public Object parseValue(String param, String value) throws Exception
    {
        Object parsedValue = null;
        if(param.equalsIgnoreCase("/reader/stats/enable"))
        {
            parsedValue = parseReaderStats(value);
        }
        else if(param.equalsIgnoreCase("/reader/metadata"))
        {
            Set<TagReadData.TagMetadataFlag> metaDataFlag =EnumSet.noneOf(TagReadData.TagMetadataFlag.class) ;
            if (value.startsWith("[") && value.endsWith("]"))
            {
            value = value.substring(1, value.length() - 1);
            String[] usermeta = value.split(",");
            for (String meta : usermeta) 
            {
                meta = meta.trim();
                if(meta.equals("ALL"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.ALL);
                }
                else if (meta.equals("READCOUNT"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.READCOUNT);
                }
                else if (meta.equals("RSSI"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.RSSI);
                }
                else if (meta.equals("ANTENNAID"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.ANTENNAID);
                }
                else if (meta.equals("FREQUENCY"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.FREQUENCY);
                }
                else if (meta.equals("TIMESTAMP"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.TIMESTAMP);
                }
                else if (meta.equals("PHASE"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.PHASE);
                }
                else if (meta.equals("PROTOCOL"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.PROTOCOL);
                }
                else if (meta.equals("DATA"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.DATA);
                }
                else if (meta.equals("GPIO_STATUS"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.GPIO_STATUS);
                }
                else if (meta.equals("GEN2_Q"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.GEN2_Q);
                }
                else if (meta.equals("GEN2_LF"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.GEN2_LF);
                }
                else if (meta.equals("GEN2_TARGET"))
                {
                    metaDataFlag.add(TagReadData.TagMetadataFlag.GEN2_TARGET);
                }
                else
                {
                    throw new IllegalArgumentException(meta + " is not a valid option.");
                }
            }
            parsedValue = metaDataFlag;
            }
        }  
        else
        {
            parsedValue = parseValue(value);
        }
        
        if(booleanParameters.contains(param.toLowerCase()))
        {
           parsedValue = parseBool(value);
        }
        else if(param.equalsIgnoreCase("/reader/read/plan"))
        {
            value = value.toLowerCase();
            if(value.startsWith("simplereadplan"))
            {
                parsedValue = parseSimpleReadPlan(value);
            }
            else
            {
                Vector<ReadPlan> rps = new Vector<ReadPlan>();
                value = value.substring("multireadplan:".length(), value.length()-1);
                String[] plans = value.split("simplereadplan:");
                for(String plan : plans)
                {
                   rps.add(parseSimpleReadPlan(plan));
                }
                parsedValue = new MultiReadPlan(rps.toArray(new ReadPlan[rps.size()]));
            }
        }
        else if(enumClass.containsKey(param.toLowerCase()))
        {
            parsedValue = Enum.valueOf(enumClass.get(param.toLowerCase()), value.toUpperCase());
        }
        else if(param.equalsIgnoreCase("/reader/gen2/accesspassword"))
        {
           parsedValue = new Gen2.Password((Integer)parseValue(value));
        }       
        else if(param.equalsIgnoreCase("/reader/gen2/q"))
        {
             Gen2.Q setQ = null;
             if(value.equalsIgnoreCase("DynamicQ"))
             {
                 setQ = new Gen2.DynamicQ();
             }
             else if(value.startsWith("StaticQ"))
             {                
                 value = value.replaceAll("[^0-9]+", " ");
                 int q = Integer.parseInt(value.trim());
                 setQ = new Gen2.StaticQ(q);
             }
             parsedValue = setQ;
        }
        return parsedValue;
    }
    
    public Object parseReaderStats(String value) throws Exception
    {
        SerialReader.ReaderStatsFlag[] readerStats = null;
        value = value.toLowerCase();
        if (value.startsWith("[") && value.endsWith("]"))
        {
            value = value.substring(1, value.length() - 1);
            String[] stats = value.split(",");
            readerStats = new SerialReader.ReaderStatsFlag[stats.length];
            int i = 0;
            for (String stat : stats)
            {
                if (stat.equals("all"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.ALL;
                }
                else if (stat.equals("antenna"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.ANTENNA;
                }
                else if (stat.equals("connectedantennaports"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.CONNECTED_ANTENNA_PORTS;
                }
                else if (stat.equals("frequency"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.FREQUENCY;
                }
                else if (stat.equals("noisefloor"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.NOISE_FLOOR_SEARCH_RX_TX_WITH_TX_ON;
                }
                else if (stat.equals("protocol"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.PROTOCOL;
                }
                else if (stat.equals("rfontime"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.RF_ON_TIME;
                }
                else if (stat.equals("temperature"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.TEMPERATURE;
                }
                else if (stat.equals("none"))
                {
                    readerStats[i] = SerialReader.ReaderStatsFlag.NONE;
                }
                else
                {
                    throw new IllegalArgumentException(stat + " is not a valid option.");
                }
                i++;
            }
        }
        else
        {
             throw new IllegalArgumentException(value +" is not a valid value for reader stats.");
        }
        return readerStats;
    }
    public Object parseValue(String value)
    {
        String s = value.toLowerCase();
        if (s.startsWith("[") && s.endsWith("]"))
        {
            String strings[];

            if (s.indexOf('[', 1) != -1)
            {
                int intArrs[][];
                Vector<int[]> intArrVec;
                int start, end;
                intArrVec = new Vector<int[]>();

                start = s.indexOf('[', 1);
                while (start != -1)
                {
                    int[] ints;
                    end = s.indexOf(']', start);
                    ints = (int[]) parseValue(s.substring(start, end + 1));
                    intArrVec.add(ints);
                    start = s.indexOf('[', end);
                }
                return intArrVec.toArray(new int[intArrVec.size()][]);
            }
            else
            {
                int ints[];

                if (s.length() > 2)
                {
                    strings = s.substring(1, s.length() - 1).split(",");
                } 
                else
                {
                    strings = new String[0];
                }

                ints = new int[strings.length];
                for (int i = 0; i < strings.length; i++)
                {
                    ints[i] = Integer.decode(strings[i]);
                }
                return ints;
            }
        }
      
        try
        {
            Integer i = (Integer) (int) (long) Long.decode(s);
            return i;
        } catch (NumberFormatException ne)
        {
        }
       return s;
    }
    
   
    public Object parseBool(String value)
    {       
        if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("high") || value.equals("1"))
        {
          return true;  
        }
        else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("low") || value.equals("0"))
        {
           return false; 
        }
        else 
        {
            throw new IllegalArgumentException("Invalid value "+ value);
        }
    }
    
    public ReadPlan parseSimpleReadPlan(String value) throws Exception
    {
        SimpleReadPlan srp = new SimpleReadPlan();

        String sub = "";
        if (value.startsWith("simplereadplan:"))
        {
            sub = value.substring("simplereadplan:".length());
        }
        else
        {
            sub = value;
        }

        if (sub.startsWith("["))
        {
            sub = sub.substring(1);
        }
        if (sub.endsWith("]"))
        {
            sub = sub.substring(0, sub.length() - 1);
        }
       
        String[] readPlanoptions = sub.split(",(?![^\\[\\]]*\\])");

        TagFilter tagFilter = null;
        TagOp tagOperation = null;
        String regex = "\\{|\\}";
        for (String option : readPlanoptions)
        {
            if (option.startsWith("antennas"))
            {
                Object obj = parseValue(option.split("=")[1]);
                srp.antennas = (int[]) obj;
            }
            else if (option.startsWith("protocol"))
            {
                srp.protocol = TagProtocol.valueOf(option.split("=")[1].toUpperCase());
            }
            else if (option.startsWith("filter"))
            {
              String filterValue = option.split("=(?![^\\[\\]]*\\])")[1];
              if(!filterValue.equalsIgnoreCase("null") && !filterValue.equals(""))
              {
                String[] filter = filterValue.split(":");
                String filterType = filter[0];
                String filterOption = filter[1];
                if (filterType.equals("gen2.select"))
                {
                    filterOption = filterOption.substring(1, filterOption.length() - 1);
                    String[] options = filterOption.split(",");
                    if(options.length != 5)
                    {
                       throw new Exception("Invalid number of arguments for ReadPlan filter"); 
                    }
                    tagFilter = new Gen2.Select
                                    (
                                       Boolean.valueOf(options[0].split("=")[1]),
                                       Gen2.Bank.valueOf(options[1].split("=")[1].replaceAll(regex, "").toUpperCase()),
                                       Integer.parseInt(options[2].split("=")[1].replaceAll(regex, "")),
                                       Integer.parseInt(options[3].split("=")[1].replaceAll(regex, "")),
                                       parseHexBytes(options[4].split("=")[1].replaceAll(regex, ""))
                                    );
                }
                else if (filterType.equals("tagdata"))
                {
                    filterOption = filterOption.substring(1, filterOption.length() - 1);
                    tagFilter = new TagData(filterOption.split("=")[1].replaceAll(regex, ""));
                }
              }
            }
            else if (option.startsWith("op"))
            {
                String Op = option.split("=(?![^\\[\\]]*\\])")[1];
                if (!Op.equalsIgnoreCase("null") && !Op.equals(""))
                {
                    String[] tagOp = Op.split(":");
                    if (tagOp[0].equals("readdata"))
                    {
                        String opValues = tagOp[1];
                        opValues = opValues.substring(1, opValues.length() - 1);
                        String[] options = opValues.split(",");
                        if(options.length != 3)
                        {
                          throw new Exception("Invalid number of arguments for ReadPlan tag op");
                        }
                       
                        tagOperation = new Gen2.ReadData
                                            (
                                              Gen2.Bank.valueOf(options[0].split("=")[1].replaceAll(regex, "").toUpperCase()),
                                              Integer.parseInt(options[1].split("=")[1].replaceAll(regex, "")),
                                              Byte.parseByte(options[2].split("=")[1].replaceAll(regex, ""))
                                            );
                    }
                }
            }
            else if (option.startsWith("usefastsearch"))
            {
                srp.useFastSearch = Boolean.parseBoolean(option.split("=")[1]);
            }
            else if (option.startsWith("weight"))
            {
                srp.weight = Integer.parseInt(option.split("=")[1]);
            }
            else
            {
                 throw new Exception("Invalid argument in ReadPlan");
            }
            srp.filter = tagFilter;
            srp.Op = tagOperation;
        }

        return srp;
    }
    
    byte[] parseHexBytes(String s)
    {
     if(s.startsWith("0x"))
     {
        s = s.substring(2);
     }
    int len = s.length();
    byte[] bytes = new byte[len/2];
    
    for (int i = 0; i < len; i+=2)
      bytes[i/2] = (byte)Integer.parseInt(
        s.substring(i, i + 2), 16);

    return bytes;
  }
    
    public void saveConfiguration(String filePath, Reader r, List<String> readOnly) throws ReaderException 
    {
        try
        {
            readOnlyParameters = new ArrayList<String>();
            readOnlyParameters.addAll(readOnly);
            Map<String, String> saveConfigData = getParametersToSave(r);
            if("".equals(filePath))
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                filePath = sdf.format(new Date())+".urac";
            }
            File file = new File(filePath);
            if(!file.exists())
            {
              file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (String key : saveConfigData.keySet())
            {
                String value = saveConfigData.get(key);
                bw.write(key + "=" + value);
                bw.newLine();
            }
            bw.close();
        } catch (Exception ex)
        {
            throw new ReaderException(ex.getMessage());
        }
    }
    
    public Map<String, String> getParametersToSave(Reader r) throws Exception
    {
       Map<String, String> saveConfigData = new HashMap<String, String>();
        try
        {
            String[] params = r.paramList();
            String paramValue = "";
            for (String param : params)
            {
                if (!readOnlyParameters.contains(param))
                {
                    Object value = "";
                    try
                    {
                        value = r.paramGet(param);
                        if (param.equalsIgnoreCase("/reader/read/plan"))
                        {
                            paramValue = formatReadPlan(value);
                        }
                        else if (param.equalsIgnoreCase("/reader/gen2/accessPassword"))
                        {
                            paramValue = formatValue(((Gen2.Password)value).value);
                        }
                        else
                        {
                            paramValue = formatValue(value);
                        }
                        saveConfigData.put(param,paramValue);
                    }
                    catch (ReaderException ex)
                    {
                       throw ex;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            if (-1 != ex.getMessage().indexOf("Timeout"))
            {
                throw ex;
            }
            else
            {
                System.out.println(ex.getMessage());
            }
          
        }
      return saveConfigData; 
    }
    
    public String formatValue(Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        if (obj.getClass().isArray())
        {
            int l = Array.getLength(obj);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < l; i++)
            {
                sb.append(formatValue(Array.get(obj, i)));
                if (i + 1 < l)
                {
                    sb.append(",");
                }
            }
            sb.append("]");
            return sb.toString();
        }
        return obj.toString();
    }
    
    public String formatReadPlan(Object value)
    {
        String readPlan="";
        ReadPlan rp = (ReadPlan) value;
        if(rp instanceof SimpleReadPlan)
        {
            readPlan = saveSimpleReadPlan(rp);
        }
        else 
        {
            readPlan += "MultiReadPlan:[";
            MultiReadPlan mrp = (MultiReadPlan) rp;
            for(ReadPlan rPlan : mrp.plans)
            {
                readPlan += saveSimpleReadPlan(rPlan) + ",";
            }
            readPlan = readPlan.substring(0,readPlan.length()-1);
            readPlan += "]";
        }       
        return readPlan;
    }
    
    public String saveSimpleReadPlan(ReadPlan rp)
    {
        String plan = "";
        plan += "SimpleReadPlan"+':'+"[";
        SimpleReadPlan srp = (SimpleReadPlan)rp;
        plan += "Antennas"+"="+ Arrays.toString(srp.antennas);
        plan += ","+"Protocol=" + srp.protocol.toString();
        if(srp.filter != null)
        {
            if(srp.filter instanceof Gen2.Select)
            {
                Gen2.Select sf =(Gen2.Select)srp.filter;
                plan += ","+ String.format("Filter=Gen2.Select:[Invert={%s},Bank={%s},BitPointer={%d},BitLength={%d},Mask={%s}]",
                        (sf.invert? "true": "false"), sf.bank,sf.bitPointer,sf.bitLength,ReaderUtil.byteArrayToHexString(sf.mask));
            }
            else
            {
              Gen2.TagData td = (Gen2.TagData) srp.filter;
              plan += "," + String.format("Filter=TagData:[EPC={%s}]", ReaderUtil.byteArrayToHexString(td.epc));
            }
        }
        else
        {
           plan += ",Filter=null";
        }
        
        if(srp.Op != null)
        {
            if (srp.Op instanceof Gen2.ReadData)
            {
                Gen2.ReadData rd = (Gen2.ReadData)srp.Op;
                plan += "," + String.format("Op=ReadData:[Bank={%s},WordAddress={%d},Len={%d}]", rd.Bank, rd.WordAddress, rd.Len);
            }
            else
            {
                plan += ",Op=null";
            }
        }
        else
        {
           plan += ",Op=null";
        }
        plan += ","+ "UseFastSearch=" + srp.useFastSearch;
        plan += ","+ "Weight=" + srp.weight + "]";
        return plan;        
    }
}
