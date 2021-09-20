package com.github.coderodde.rodioning;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeEntry;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

/**
 *
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 19, 2021)
 * @since 1.6 (Sep 19, 2021)
 */
@WebServlet(name="RodioningServlet", urlPatterns={"/downloadgh"})
public final class RodioningServlet extends HttpServlet {
   
    private static final Logger LOGGER =
            Logger.getLogger(RodioningServlet.class.getSimpleName());
    
    private static final String USER_NAME_PARAMETER_NAME = "username";
    private static final String PASSWORD_ENVIRONMENT_VARIABLE_NAME = 
            "RODIONING_PASSWORD"; 
    
   private static final String DEFAULT_USER_NAME = "coderodde";
    
   private static final Gson GSON = new Gson();
   
    @Override
    protected void doPost(HttpServletRequest request, 
                          HttpServletResponse response)
    throws ServletException, IOException {
        String userName = request.getParameter(USER_NAME_PARAMETER_NAME);
        
        if (userName == null) {
            userName = DEFAULT_USER_NAME;
        }
        
        String password = System.getenv(PASSWORD_ENVIRONMENT_VARIABLE_NAME);
        password = "Exp10r1ngG17Hu8";
        RodioningResult rodioningResult = new RodioningResult();
        rodioningResult.succeeded = true;
        
        if (password == null) {
            rodioningResult.succeeded = false;
            response.getWriter().println(GSON.toJson(rodioningResult));
            return;
        }
        
        GitHub github =
                new GitHubBuilder().withOAuthToken("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", "coderodde").build();
//                new GitHubBuilder().withPassword(userName, password).build();
        
        Map<String, GHRepository> myRepositoryMap = 
                github
                        .getMyself()
                        .getAllRepositories();
        
        List<GHRepository> ghRepositories = 
                new ArrayList<>(myRepositoryMap.size());
        
        try (PrintWriter out = response.getWriter()) {
            for (Map.Entry<String, GHRepository> e  
                    : myRepositoryMap.entrySet()) {
                ghRepositories.add(e.getValue());
            }
        }
        
        processRepositories(ghRepositories, request, response);
    }
    
    private static void processRepositories(List<GHRepository> ghRepositories,
                                            HttpServletRequest requesst,
                                            HttpServletResponse response) 
    throws IOException {
        
        Collections.shuffle(ghRepositories);
        List<GHTreeEntry> allGHTreeEntries = new ArrayList<>();
        
        for (GHRepository ghRepository : ghRepositories) {
            allGHTreeEntries.addAll(processRepository(ghRepository));
            System.out.println(ghRepository.getName());
        }
        
        try (PrintWriter out = response.getWriter()) {
            String programTextContents = downloadAll(allGHTreeEntries);
            out.println(programTextContents);
            
            System.out.println(programTextContents);
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
            throw ex;
        }
    }
    
    private static String downloadAll(List<GHTreeEntry> ghTreeEntries) 
    throws UnsupportedEncodingException, IOException {
        
        StringBuilder stringBuilder = new StringBuilder();
        
        for (GHTreeEntry ghTreeEntry : ghTreeEntries) {
            stringBuilder.append(myRead(ghTreeEntry.asBlob().read()));
        }
        
        return stringBuilder.toString();
    }
    
    private static String myRead(InputStream inputStream) 
    throws UnsupportedEncodingException, IOException {
        
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
       
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        
        return result.toString("UTF-8");
    }
    
    private static List<GHTreeEntry> 
        processRepository(GHRepository ghRepository) throws IOException {
            
        String branch = ghRepository.getDefaultBranch();
        GHTree tree;
        
        try {
            tree = ghRepository.getTreeRecursive(branch, 1);
        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
            throw ex;
        }
        
        List<GHTreeEntry> allGHTreeEntries = new ArrayList<>();
        loadGHTreeEntries(allGHTreeEntries, tree);
        return allGHTreeEntries;
    }
    
    private static void loadGHTreeEntries(
            List<GHTreeEntry> allGHTreeEntries, GHTree ghTree) 
    throws IOException {
        
        List<GHTreeEntry> ghTreeEntries = ghTree.getTree();
        
        for (GHTreeEntry ghTreeEntry : ghTreeEntries) {
            loadGHTreeEntry(allGHTreeEntries, ghTreeEntry);
        }
    }
    
    private static void loadGHTreeEntry(List<GHTreeEntry> allGHTreeEntries,
                                        GHTreeEntry ghTreeEntry) 
    throws IOException {
        if (ghTreeEntry.getType().equals("blob")) {
            allGHTreeEntries.add(ghTreeEntry);
            return;
        }
        
        for (GHTreeEntry childGHTreeEntry : ghTreeEntry.asTree().getTree()) {
            loadGHTreeEntry(allGHTreeEntries, childGHTreeEntry);
        }
    }
        
    private static final class RodioningResult {
        boolean succeeded;
        List<String> textFiles;
    }
}
