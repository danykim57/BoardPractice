package com.study;


import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
public class ExcelController {
  public void createExcel() {
    //.xls 확장자 지원
    HSSFWorkbook wb = null;
    HSSFSheet sheet = null;
    Row row = null;
    Cell cell = null;

    //.xlsx 확장자 지원
    XSSFWorkbook xssfWb = null; // .xlsx
    XSSFSheet xssfSheet = null; // .xlsx
    XSSFRow xssfRow = null; // .xlsx
    XSSFCell xssfCell = null;// .xlsx

    try {
      int rowNo = 0; // 행 갯수
      // 워크북 생성
      xssfWb = new XSSFWorkbook();
      xssfSheet = xssfWb.createSheet("엑셀 테스트"); // 워크시트 이름

      //헤더용 폰트 스타일
      XSSFFont font = xssfWb.createFont();
      font.setFontName(HSSFFont.FONT_ARIAL); //폰트스타일
      font.setFontHeightInPoints((short)14); //폰트크기
      font.setBold(true); //Bold 유무

      //테이블 타이틀 스타일
      CellStyle cellStyle_Title = xssfWb.createCellStyle();

      xssfSheet.setColumnWidth(3, (xssfSheet.getColumnWidth(3))+(short)2048); // 3번째 컬럼 넓이 조절
      xssfSheet.setColumnWidth(4, (xssfSheet.getColumnWidth(4))+(short)2048); // 4번째 컬럼 넓이 조절
      xssfSheet.setColumnWidth(5, (xssfSheet.getColumnWidth(5))+(short)2048); // 5번째 컬럼 넓이 조절

      xssfSheet.setColumnWidth(8, (xssfSheet.getColumnWidth(8))+(short)4096); // 8번째 컬럼 넓이 조절

      cellStyle_Title.setFont(font); // cellStle에 font를 적용
      cellStyle_Title.setAlignment(HorizontalAlignment.CENTER); // 정렬

      //셀병합
      xssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8)); //첫행, 마지막행, 첫열, 마지막열( 0번째 행의 0~8번째 컬럼을 병합한다)
      //타이틀 생성
      xssfRow = xssfSheet.createRow(rowNo++); //행 객체 추가
      xssfCell = xssfRow.createCell((short) 0); // 추가한 행에 셀 객체 추가
      xssfCell.setCellStyle(cellStyle_Title); // 셀에 스타일 지정
      xssfCell.setCellValue("타이틀 입니다."); // 데이터 입력

      xssfRow = xssfSheet.createRow(rowNo++);  // 빈행 추가

      CellStyle cellStyle_Body = xssfWb.createCellStyle();
      cellStyle_Body.setAlignment(HorizontalAlignment.LEFT);

      //헤더 생성
      xssfSheet.addMergedRegion(new CellRangeAddress(rowNo, rowNo, 0, 1)); //첫행,마지막행,첫열,마지막열
      xssfRow = xssfSheet.createRow(rowNo++); //헤더 01
      xssfCell = xssfRow.createCell((short) 0);
      xssfCell.setCellStyle(cellStyle_Body);
      xssfCell.setCellValue("헤더01 셀01");
      xssfCell = xssfRow.createCell((short) 8);
      xssfCell.setCellStyle(cellStyle_Body);
      xssfCell.setCellValue("헤더01 셀08");
      xssfRow = xssfSheet.createRow(rowNo++); //헤더 02
      xssfCell = xssfRow.createCell((short) 0);
      xssfCell.setCellStyle(cellStyle_Body);
      xssfCell.setCellValue("헤더02 셀01");
      xssfCell = xssfRow.createCell((short) 8);
      xssfCell.setCellStyle(cellStyle_Body);
      xssfCell.setCellValue("헤더02 셀08");
      xssfRow = xssfSheet.createRow(rowNo++); //헤더 03
      xssfCell = xssfRow.createCell((short) 0);
      xssfCell.setCellStyle(cellStyle_Body);
      xssfCell.setCellValue("헤더03 셀01");
      xssfCell = xssfRow.createCell((short) 8);
      xssfCell.setCellStyle(cellStyle_Body);
      xssfCell.setCellValue("헤더03 셀08");
      xssfRow = xssfSheet.createRow(rowNo++); //헤더 04
      xssfCell = xssfRow.createCell((short) 0);
      xssfCell.setCellStyle(cellStyle_Body);
      xssfCell.setCellValue("헤더04 셀01");
      xssfCell = xssfRow.createCell((short) 8);
      xssfCell.setCellStyle(cellStyle_Body);
      xssfCell.setCellValue("헤더04 셀08");

      //테이블 스타일 설정
      CellStyle cellStyle_Table_Center = xssfWb.createCellStyle();
      cellStyle_Table_Center.setBorderTop(BorderStyle.THIN); //테두리 위쪽
      cellStyle_Table_Center.setBorderBottom(BorderStyle.THIN); //테두리 아래쪽
      cellStyle_Table_Center.setBorderLeft(BorderStyle.THIN); //테두리 왼쪽
      cellStyle_Table_Center.setBorderRight(BorderStyle.THIN); //테두리 오른쪽
      cellStyle_Table_Center.setAlignment(HorizontalAlignment.CENTER);

      xssfRow = xssfSheet.createRow(rowNo++);
      xssfCell = xssfRow.createCell((short) 0);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀1");
      xssfCell = xssfRow.createCell((short) 1);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀2");
      xssfCell = xssfRow.createCell((short) 2);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀3");
      xssfCell = xssfRow.createCell((short) 3);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀4");
      xssfCell = xssfRow.createCell((short) 4);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀5");
      xssfCell = xssfRow.createCell((short) 5);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀6");
      xssfCell = xssfRow.createCell((short) 6);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀7");
      xssfCell = xssfRow.createCell((short) 7);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀8");
      xssfCell = xssfRow.createCell((short) 8);
      xssfCell.setCellStyle(cellStyle_Table_Center);
      xssfCell.setCellValue("테이블 셀9");

      String localFile = "C:\\" + "테스트_엑셀" + ".xlsx";

      /**
       *     // 함수값 재 설정
       *
       *     for (int i = 1; i <= 12; i++) {
       *
       *       getCell(sheet, i, 1).setCellFormula(String.format("AVERAGE(C%d:D%d)", i + 1, i + 1));
       *
       *     }
       */

      File file = new File(localFile);
      FileOutputStream fos = null;
      fos = new FileOutputStream(file);
      xssfWb.write(fos);

      if (xssfWb != null)	xssfWb.close();
      if (fos != null) fos.close();

      //ctx.put("FILENAME", "입고상세출력_"+ mapList.get(0).get("PRINT_DATE"));
      //if(file != null) file.deleteOnExit();
      /***
       * 폰트 스타일과 문자 삽입은 셀 1개에 적용해도 되지만
       * 테두리나 셀 색깔과 같은 것은 병합된 모든 셀에 해주어야함
       *
       * 생성 후 저장이 아닌 다운로드 기능을 할시에는 HttpServletResponse에 담아서 보내야함
       *
       * 파일 이름 Response 헤더에 넣을 경우 Response header는 utf8을 지원하지 않는다.
       *
       * utf8으로 할 경우에 URIEncoder를 써서 bytecode로 변환 시켜서 프론트로 넘겨주어야한다.
       *
       * 프론트에서는 이걸 URIDecoder를 이용하여서 다시 UTF8으로 바꾸어 주어야한다.
       */
    }
    catch(Exception e){
      System.out.println(e.getMessage());

    }
  }
}
