package com.reneseses.empaques.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.reneseses.empaques.domain.Repechaje;
import com.reneseses.empaques.domain.Solicitud;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;
import com.reneseses.empaques.domain.service.RepechajeServiceImpl;
import com.reneseses.empaques.domain.service.SolicitudServiceImpl;
import com.reneseses.empaques.domain.service.UsuarioServiceImpl;
import com.reneseses.empaques.enums.RegimenTurnoEnum;
import com.reneseses.empaques.enums.TipoUsuarioEnum;
import com.reneseses.empaques.formularios.ImagenForm;

@RequestMapping("/member/data")
@Controller
public class DataController {

    @Autowired
    private UsuarioServiceImpl usuarioServiceImpl;
    
    @Autowired
    private SolicitudServiceImpl solicitudServiceImpl;
    
    @Autowired
    private RepechajeServiceImpl repechajeServiceImpl;
    
    @RequestMapping(value="xls")
    public String getUsuariosFile(HttpServletResponse response){
    	Workbook wb= new XSSFWorkbook();
    	Sheet sheet = wb.createSheet("Empaques");
    	Row headerRow = sheet.createRow(0);
    	Cell cell = headerRow.createCell(0); cell.setCellValue("Numero");
        cell = headerRow.createCell(1); cell.setCellValue("Nombre");
        cell = headerRow.createCell(2); cell.setCellValue("Rut");
        cell = headerRow.createCell(3); cell.setCellValue("Password");
        cell = headerRow.createCell(4); cell.setCellValue("Email");
        cell = headerRow.createCell(5); cell.setCellValue("Celular");
        cell = headerRow.createCell(6); cell.setCellValue("Tipo");
        cell = headerRow.createCell(7); cell.setCellValue("Regimen");
        cell = headerRow.createCell(8); cell.setCellValue("Turnos");
        cell = headerRow.createCell(9); cell.setCellValue("Ultima solicitud");
        cell = headerRow.createCell(10); cell.setCellValue("Prioridad");
        cell = headerRow.createCell(11); cell.setCellValue("Fecha nacimiento");
        cell = headerRow.createCell(12); cell.setCellValue("Carrera");
        cell = headerRow.createCell(13); cell.setCellValue("Universidad");
        
        List<Usuario> list= usuarioServiceImpl.findAllUsuarios();
        Row row;
        int rownum = 1;
        for(Usuario user: list){
        	row = sheet.createRow(rownum);
        	cell = row.createCell(0); cell.setCellValue(user.getId().getNumero());
            cell = row.createCell(1); cell.setCellValue(user.getNombre());
            cell = row.createCell(2); cell.setCellValue(user.getRut());
            cell = row.createCell(3); cell.setCellValue(user.getPassword());
            cell = row.createCell(4); cell.setCellValue(user.getEmail());
            cell = row.createCell(5); cell.setCellValue(user.getCelular());
            cell = row.createCell(6); cell.setCellValue(user.getTipo().toString());
            cell = row.createCell(7); cell.setCellValue(user.getRegimen().toString());
            cell = row.createCell(8); cell.setCellValue("");
            if(user.getLastSolicitud() != null){
            	cell = row.createCell(9); cell.setCellValue(user.getLastSolicitud());
            }
            cell = row.createCell(10); cell.setCellValue(user.getPrioridad());
            if(user.getFechaNacimiento() != null){
            	cell = row.createCell(11); cell.setCellValue(user.getFechaNacimiento());
            }
            cell = row.createCell(12); cell.setCellValue(user.getCarrera());
            cell = row.createCell(13); cell.setCellValue(user.getUniversidad());
            rownum++;
        }
    	
        sheet = wb.createSheet("Solicitudes");
    	headerRow = sheet.createRow(0);
    	cell = headerRow.createCell(0); cell.setCellValue("Fecha");
        cell = headerRow.createCell(1); cell.setCellValue("Usuario");
        cell = headerRow.createCell(2); cell.setCellValue("Turnos");
        
        List<Solicitud> sols= solicitudServiceImpl.findAllSolicituds();
        rownum = 1;
        for(Solicitud sol: sols){
        	row = sheet.createRow(rownum);
            if(sol.getFecha() != null){
            	cell = row.createCell(0); cell.setCellValue(sol.getFecha());
            }
            cell = row.createCell(1); cell.setCellValue(sol.getUsuario().getNumero());
            cell = row.createCell(2); cell.setCellValue(sol.getTurnos().toString());
            rownum++;
        }
        
        sheet = wb.createSheet("Repechajes");
    	headerRow = sheet.createRow(0);
    	cell = headerRow.createCell(0); cell.setCellValue("Fecha");
        cell = headerRow.createCell(1); cell.setCellValue("Usuario");
        cell = headerRow.createCell(2); cell.setCellValue("Turnos");
        
        List<Repechaje> reps= repechajeServiceImpl.findAllRepechajes();
        rownum = 1;
        for(Repechaje rep: reps){
        	row = sheet.createRow(rownum);
            if(rep.getFecha() != null){
            	cell = row.createCell(0); cell.setCellValue(rep.getFecha());
            }
            cell = row.createCell(1); cell.setCellValue(rep.getUsuario().getNumero());
            cell = row.createCell(2); cell.setCellValue(rep.getTurnos().toString());
            rownum++;
        }
        
        try {
            response.setHeader("Content-Disposition", "inline;filename=\"usuarios.xlsx\"");
            OutputStream out = response.getOutputStream();
            response.setContentType("xlsx");
            wb.write(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "gracias";
    }
    
    @RequestMapping(value= "/upload", params="form")
    public String upload(Model uiModel){
    	uiModel.addAttribute("data", new ImagenForm());
    	return "member/data/upload";
    }
    
    private void cellContent(Cell cell, int type, BasicDBObject doc, String column) {
        switch(type) {
            case Cell.CELL_TYPE_STRING:
                doc.put(column, cell.getRichStringCellValue().getString());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    DateFormat formater = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    doc.put(column, formater.format(cell.getDateCellValue()));
                } else {
                    doc.put(column, cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                doc.put(column, cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                doc.put(column, cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                doc.put(column, "");
                break;
            default:
        }
    }
    
    @RequestMapping(value="/upload", method = RequestMethod.POST)
    public String xlsUpload(ImagenForm data, BindingResult bindingResult, @RequestParam("content") MultipartFile content, Model uiModel) {
    	Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	
        Usuario usuario;
        Cell cell;
    	try {
			byte[] array = content.getBytes();
			InputStream inp = new ByteArrayInputStream(array);
			Workbook wb = WorkbookFactory.create(inp);
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            Sheet thisSheet = wb.getSheetAt(0);
            BasicDBObject doc = new BasicDBObject();
            for (int i = 1; i <= thisSheet.getLastRowNum(); i++) {
                usuario = new Usuario();
                Row row = thisSheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < 14; j++) {
                        cell = row.getCell(j);
                        if (cell != null) {
                            int type = evaluator.evaluateInCell(cell).getCellType();
                            cellContent(cell, type, doc, String.valueOf(j));
                        } else 
                            doc.put(String.valueOf(j), "");
                    }
                } else break;
                
                UsuarioId id= new UsuarioId();
                
                id.setNumero(doc.getInt("0"));
                id.setSupermercado(principal.getId().getSupermercado());
                
                usuario.setId(id);
                usuario.setNombre(doc.getString("1"));
                usuario.setRut(doc.getString("2"));
                usuario.setPassword(doc.getString("3"));
                usuario.setEmail(doc.getString("4"));
                usuario.setCelular(doc.getString("5"));
                usuario.setTipo(TipoUsuarioEnum.valueOf(doc.getString("6")));
                usuario.setRegimen(RegimenTurnoEnum.valueOf(doc.getString("7")));
                usuario.setPrioridad(doc.getInt("10"));
                usuario.setCarrera(doc.getString("12"));
                usuario.setUniversidad(doc.getString("13"));
                if (doc.getString("9") != "") {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    try {
                        usuario.setLastSolicitud(formatter.parse(doc.getString("9")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (doc.getString("11") != "") {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    try {
                        usuario.setFechaNacimiento(formatter.parse(doc.getString("11")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                
                usuarioServiceImpl.saveUsuario(usuario);
            }
            thisSheet = wb.getSheetAt(1);
            doc = new BasicDBObject();
            for (int i = 1; i <= thisSheet.getLastRowNum(); i++) {
            	Row row = thisSheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < 3; j++) {
                        cell = row.getCell(j);
                        if (cell != null) {
                            int type = evaluator.evaluateInCell(cell).getCellType();
                            cellContent(cell, type, doc, String.valueOf(j));
                        } else 
                            doc.put(String.valueOf(j), "");
                    }
                } else break;
                
                Solicitud sol= new Solicitud();
                if (doc.getString("0") != "") {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    try {
                        sol.setFecha(formatter.parse(doc.getString("0")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                
                UsuarioId id= new UsuarioId();
                
                id.setNumero(doc.getInt("1"));
                id.setSupermercado(principal.getId().getSupermercado());
                
                sol.setUsuario(id);
                BasicDBList ja= (BasicDBList) JSON.parse(doc.getString("2"));
                sol.setTurnos(ja);
                solicitudServiceImpl.saveSolicitud(sol);
            }
            thisSheet = wb.getSheetAt(2);
            doc = new BasicDBObject();
            for (int i = 1; i <= thisSheet.getLastRowNum(); i++) {
            	Row row = thisSheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < 3; j++) {
                        cell = row.getCell(j);
                        if (cell != null) {
                            int type = evaluator.evaluateInCell(cell).getCellType();
                            cellContent(cell, type, doc, String.valueOf(j));
                        } else 
                            doc.put(String.valueOf(j), "");
                    }
                } else break;
                
                Repechaje rep= new Repechaje();
                if (doc.getString("0") != "") {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    try {
                        rep.setFecha(formatter.parse(doc.getString("0")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                
                UsuarioId id= new UsuarioId();
                
                id.setNumero(doc.getInt("1"));
                id.setSupermercado(principal.getId().getSupermercado());
                
                rep.setUsuario(id);
                
                BasicDBList ja= (BasicDBList) JSON.parse(doc.getString("2"));
                
                rep.setTurnos(ja);
                repechajeServiceImpl.saveRepechaje(rep);
            }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return "gracias";
    }
    
}