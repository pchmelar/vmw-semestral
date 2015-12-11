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
import cz.cvut.fit.vmw.integration.PhotoFileDAO;
import cz.cvut.fit.vmw.model.PhotoFile;
import cz.cvut.fit.vmw.model.UploadPhotoFile;
import cz.cvut.fit.vmw.model.UploadPhotoFile64;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

/**
 * REST Web Service
 *
 * @author jan
 */
@Path("/photo")
public class PhotoResource {
//    private static final String PATH = "/usr/local/share/jboss_8_1/surfphotos/";

    @Inject
    private PhotoFileDAO photoFileDAO;
    private static final String PATH = "/Users/jan/Documents/jboss_8_1/surfphotos/"; // macbook development path
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(PhotoResource.class.getName());

    @Context
    private UriInfo context;

    public PhotoResource() {
    }

    @GET
    @Produces("application/json")
    public Response getAllPhotos() {
        List<PhotoFile> result = photoFileDAO.findAll();
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Date.class, ser)
                .create();
        return Response.ok(gson.toJson(result)).encoding("UTF-8").build();
    }
    
    @GET
    @Path("find10/")
    @Produces("application/json")
    public Response findBest10ToID(@PathParam("id") final Integer id) {
        List<PhotoFile> result = new ArrayList<>();
        
        
        
        
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
        return Response.ok(gson.toJson(result)).encoding("UTF-8").build();
    }

//    @GET
//    @Path("show/id/{id}")
//    @Produces(MediaType.APPLICATION_OCTET_STREAM)
//    public Response getPhoto(@PathParam("id") final Integer id, @Context HttpServletRequest req) {
//        PhotoFile photo = photoFileDAO.find(id);
//        if (photo == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//        try {
//            return Response.ok(photo.getData()).header("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(photo.getId().toString()+".jpeg", "UTF-8")).build();
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(PhotoResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return Response.serverError().build();
//    }
    @GET
    @Path("show/id/{id}")
    @Produces({"image/png", "image/jpeg"})
//        @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPhoto(@PathParam("id") final Integer id, @Context HttpServletRequest req) {
        PhotoFile photo = photoFileDAO.find(id);
        if (photo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
//            return Response.ok(new ByteArrayInputStream(photo.getData())).header("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(photo.getId().toString()+".jpeg", "UTF-8")).build();
            return Response.ok(new ByteArrayInputStream(photo.getData())).build();
        } catch (Exception ex) {
            Logger.getLogger(PhotoResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.serverError().build();
    }
//    image/png

    @POST
    @Path("upload")
    @Consumes("multipart/form-data")
    @Produces("application/json")
    public Response uploadPhoto(@MultipartForm UploadPhotoFile uploadedPhoto) {
        PhotoFile photo = new PhotoFile();
        photo.setCreateDate(new Date());
        photo.setData(uploadedPhoto.getData());
        photo.setId(null);
        
        LOG.info(uploadedPhoto.getData().toString());
        photoFileDAO.create(photo);
        List<PhotoFile> result = photoFileDAO.findAll();
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .registerTypeAdapter(Date.class, ser)
                .create();
        return Response.ok(gson.toJson(result)).encoding("UTF-8").build();
    }
    
    @POST
    @Path("upload64")
    @Consumes("application/json")
    @Produces("application/json")
    public Response uploadPhoto64(UploadPhotoFile64 photoBase64) {
        byte[] photoData = null;
//        try {
//            photoData = Base64.getDecoder().decode(URLDecoder.decode(photoBase64.getData(), "UTF-8").getBytes(StandardCharsets.ISO_8859_1));
            photoData = Base64.getDecoder().decode(photoBase64.getData().getBytes(StandardCharsets.UTF_8));
//            Base64.getDecoder().de
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(PhotoResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
        PhotoFile photo = new PhotoFile();
        photo.setCreateDate(new Date());
        photo.setData(photoData);
        photo.setId(null);
        photoFileDAO.create(photo);
        LOG.info("UPLOADED");
        List<PhotoFile> result = photoFileDAO.findAll();
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .registerTypeAdapter(Date.class, ser)
                .create();
//        LOG.info(gson.toJson(result));
        return Response.ok(gson.toJson(result)).encoding("UTF-8").build();
    }
    
//    @PUT
//    @Path("{name}")
//    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
//    public Response saveFile(@PathParam("name") String fileName, InputStream content) {
//        try {
//            File file = new File(PATH + fileName);
//            if (!file.exists()) {
//                file.createNewFile();
//                byte[] buffer = new byte[4096];
//                int read;
//                try (FileOutputStream fos = new FileOutputStream(file)) {
//                    while ((read = content.read(buffer)) > 0) {
//                        fos.write(buffer, 0, read);
//                    }
//                }
//                Logger.getLogger(getClass().getName()).log(Level.INFO, "New file {0} was created", fileName);
//            }
//
//        } catch (Exception ex) {
//            Logger.getLogger(PhotoResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
//            @Override
//            public JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
//                return src == null ? null : new JsonPrimitive(src.getTime());
//            }
//        };
//        Gson gson = new GsonBuilder()
//                .serializeNulls()
//                .setPrettyPrinting()
//                .registerTypeAdapter(Date.class, ser)
//                .create();
//        return Response.ok(gson.toJson(filesResult)).encoding("UTF-8").build();
//
//    }

    /**
     * PUT method for updating or creating an instance of FileResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
   
}
