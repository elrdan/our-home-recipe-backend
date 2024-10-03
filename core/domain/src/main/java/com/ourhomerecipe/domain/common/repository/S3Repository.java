package com.ourhomerecipe.domain.common.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class S3Repository {
	private final AmazonS3 s3Client;

	@Value("${s3.bucket.name}")
	private String bucketName;

	/**
	 * s3 이미지 업로드
	 */
	public String uploadFile(MultipartFile file) throws IOException {
		//
		String fileKey = System.currentTimeMillis() + "_" + file.getOriginalFilename();

		try {
			File fileObj = convertMultiPartFileToFile(file);
			s3Client.putObject(new PutObjectRequest(bucketName, fileKey, fileObj));
			fileObj.delete(); // 임시 파일 삭제

			// 엔드 포인트 Url
			return s3Client.getUrl(bucketName, fileKey).toString();

		} catch (IOException e) {
			throw e;
		}
	}

	private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
		// MultipartFile의 파일명을 사용하여 새로운 File 객체를 생성
		File convertedFile = new File(file.getOriginalFilename());

		// FileOutputStream을 사용해 MultipartFile의 바이트 데이터를 새로운 파일에 작성
		FileOutputStream fos = new FileOutputStream(convertedFile);
		fos.write(file.getBytes());
		fos.close();
		return convertedFile;
	}
}