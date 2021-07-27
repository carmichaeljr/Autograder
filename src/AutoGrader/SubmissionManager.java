package AutoGrader;

import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.exception.ZipException;

public class SubmissionManager {
	private HashSet<String> submissions;
	private static final SubmissionManager inst=new SubmissionManager();

	public static void load() throws AutoGraderException {
		SubmissionManager.inst.renameSubmissions();
		SubmissionManager.inst.setSubmissions();
		SubmissionManager.inst.createTempSubmissionDir();
	}

	public static boolean prepareForGrading(String student){
		boolean rv=SubmissionManager.inst.submissions.contains(student);
		rv&=(rv)? SubmissionManager.inst.moveToTempDir(student): rv;
		rv&=(rv)? SubmissionManager.inst.extractFiles(student): rv;
		return rv;
	}

	public static String getCodeFile(String student){
		if (SubmissionManager.inst.submissions.contains(student)){
			String temp=String.format("%s/%s",Settings.getHWData().getCleanedSubmissionsDir(),student);
			return SubmissionManager.inst.getFileByPrecidence(Settings.getHWData().getAcceptedCodeFiles(),temp);
		}
		return null;
	}

	public static String getReadmeFile(String student){
		if (SubmissionManager.inst.submissions.contains(student)){
			String temp=String.format("%s/%s",Settings.getHWData().getCleanedSubmissionsDir(),student);
			return SubmissionManager.inst.getFileByPrecidence(Settings.getHWData().getAcceptedReadmeFiles(),temp);
		}
		return null;
	}

	public static void cleanUpAfterGrading(String student){
		SubmissionManager.inst.deleteFromTempDir(student);
	}

	public static HashSet<String> getStudents(){
		return SubmissionManager.inst.submissions;
	}

	public static void cleanUp(){
		SubmissionManager.inst.deleteDirectory(Settings.getHWData().getCleanedSubmissionsDir());
	}

	private SubmissionManager(){
		this.submissions=new HashSet<String>();
	}

	private void renameSubmissions() throws AutoGraderException {
		try {
			ZipFile zf=new ZipFile(Settings.getHWData().getZippedSubmissions());
			List<FileHeader> fileHeaders=zf.getFileHeaders();
			for (int i=0; i<fileHeaders.size(); i++){
				String name=fileHeaders.get(i).getFileName();
				int index=name.indexOf('_');
				if (index>0){
					zf.renameFile(name,String.format("%s.zip",name.substring(0,index)));
				}
				//System.out.println(name+" "+String.format("%s.zip",name.substring(0,index)));
			}
		} catch (ZipException e){
			throw new AutoGraderException("An error occurred renaming students submissions.");
		}
	}

	private void setSubmissions() throws AutoGraderException {
		try {
			List<FileHeader> fh=new ZipFile(Settings.getHWData().getZippedSubmissions()).getFileHeaders();
			for (int i=0; i<fh.size(); i++){
				String name=fh.get(i).getFileName();
				int index=name.indexOf('.');
				this.submissions.add(name.substring(0,index));
			}
			//System.out.println(this.submissions.toString());
		} catch (ZipException e){
			throw new AutoGraderException("An error occurred opening the submission file.");
		}
	}

	private void createTempSubmissionDir() throws AutoGraderException {
		try {
			Files.createDirectories(Paths.get(Settings.getHWData().getCleanedSubmissionsDir()));
		} catch (IOException e){
			throw new AutoGraderException("An error occured creating the temp submissions directory.");
		}
	}

	private boolean moveToTempDir(String submission){
		try {
			String fullSubName=String.format("%s.zip",submission);
			new ZipFile(Settings.getHWData().getZippedSubmissions()).
				extractFile(fullSubName,Settings.getHWData().getCleanedSubmissionsDir());
			return true;
		} catch (ZipException e){
			System.out.print(e);
			Print.warning(String.format("An error occurred extracting submission %s from the submissions file.",submission));
			return false;
		}
	}

	private boolean extractFiles(String submission){
		try {
			String fullSubDir=String.format("%s/%s",Settings.getHWData().getCleanedSubmissionsDir(),submission);
			String zippedSubName=String.format("%s.zip",fullSubDir);
			ZipFile zf=new ZipFile(zippedSubName);
			List<FileHeader> fh=zf.getFileHeaders();
			for (int i=0; i<fh.size(); i++){
				String path=fh.get(i).getFileName();
				String ext=this.getFileExtension(path);
				//TODO - Check that the path does not contain the grading script
				if (ext.length()>0 && !path.contains("MACOS") &&
				    (Settings.getHWData().getAcceptedCodeFiles().contains(ext) ||
				     Settings.getHWData().getAcceptedReadmeFiles().contains(ext))){
					String name=Paths.get(path).getFileName().toString();
					zf.extractFile(path,fullSubDir,name);
				}
			}
			return true;
		} catch (IOException e){
			Print.warning(String.format("An error occurred extracting submission files from the submission %s.",submission));
			return false;
		}
	}

	private String getFileByPrecidence(ArrayList<String> typePrecidence, String dir){
		String rv=null;
		File folder=new File(dir);
		for (int i=0; i<typePrecidence.size() && rv==null; i++){
			final String iterType=typePrecidence.get(i);
			FilenameFilter filter=new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name){
					return name.endsWith(String.format(".%s",iterType));
				}
			};
			File[] files=folder.listFiles(filter);
			if (files!=null && files.length>0){
				rv=files[0].getAbsolutePath();
			}
		}
		return rv;
	}

	private void deleteFromTempDir(String submission){
		try {
			this.deleteDirectory(String.format("%s/%s",Settings.getHWData().getCleanedSubmissionsDir(),submission));
			Files.delete(Paths.
				get(String.format("%s/%s.zip",Settings.getHWData().getCleanedSubmissionsDir(),submission)));
		} catch (IOException e) {
			Print.warning(String.format("An error occurred deleting %s's submission from the temp cleaned submissions directory.",submission));
		}
	}

	private void deleteDirectory(String dir){
		try {
			Path path = Paths.get(dir);
			if (Files.exists(path)){
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}
					
					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
				});
			}
		} catch (IOException e){
			Print.warning(String.format("The directory '%s' was unable to be deleted.",dir));
		}
	}

	private String getFileExtension(String path){
		String rv="";
		int dotIndex=path.lastIndexOf('.');
		int dirIndex=Math.max(path.lastIndexOf('/'),path.lastIndexOf('\\'));
		if (dotIndex>dirIndex){
			rv=path.substring(dotIndex+1);
		}
		return rv;
	}
}
