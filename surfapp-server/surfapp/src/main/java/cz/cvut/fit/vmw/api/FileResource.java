/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.vmw.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;

/**
 * REST Web Service
 *
 * @author jan
 */
@Path("/fs")
public class FileResource {
//    private static final String PATH = "/usr/local/share/jboss_8_1/surfphotos/";

    private static final String PATH = "/Users/jan/Documents/jboss_8_1/surfphotos/"; // macbook development path
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(FileResource.class.getName());

    @Context
    private UriInfo context;

    public FileResource() {
    }

    @GET
    @Path("sf")
    @Produces("text/plain")
    public String showFiles() {
        File dir = new File(PATH);
        StringBuilder sb = new StringBuilder();
        ArrayList<File> files = new ArrayList<>(Arrays.asList(dir.listFiles()));
        for (File f : files) {
            sb.append(f.getName());
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<AppFile> findAllFiles(String path) {
        File dir = new File(path);
        List<AppFile> filesResult = new ArrayList<AppFile>();
        ArrayList<File> files = new ArrayList<>(Arrays.asList(dir.listFiles()));
        for (File f : files) {
            AppFile file = new AppFile();
            file.setName(f.getName());
            LOG.info(file.getName());
            file.setExt(FilenameUtils.getExtension(f.getName()));
            file.setSize(f.length());
            file.setDate(new Date(f.lastModified()));
            filesResult.add(file);
        }
        return filesResult;
    }

    /**
     * Retrieves representation of an instance of
     * cz.cvut.fit.mdw.fileserver.FileResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public Response getAllFiles() {
        List<AppFile> filesResult = findAllFiles(PATH);
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Date.class, ser)
                .create();
        return Response.ok(gson.toJson(filesResult)).encoding("UTF-8").build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("name") final String fileName, @Context HttpServletRequest req) {
        File file = new File(PATH + fileName);
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            return Response.ok(file).header("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), "UTF-8")).build();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.serverError().build();
    }

    @PUT
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response saveFile(@PathParam("name") String fileName, InputStream content) {
        try {
            File file = new File(PATH + fileName);
            if (!file.exists()) {
                file.createNewFile();
                byte[] buffer = new byte[4096];
                int read;
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    while ((read = content.read(buffer)) > 0) {
                        fos.write(buffer, 0, read);
                    }
                }
                Logger.getLogger(getClass().getName()).log(Level.INFO, "New file {0} was created", fileName);
            }

        } catch (Exception ex) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<AppFile> filesResult = findAllFiles(PATH);
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Date.class, ser)
                .create();
        return Response.ok(gson.toJson(filesResult)).encoding("UTF-8").build();

    }

    /**
     * PUT method for updating or creating an instance of FileResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
