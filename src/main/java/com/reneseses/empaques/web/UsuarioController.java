package com.reneseses.empaques.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.reneseses.empaques.domain.Bloque;
import com.reneseses.empaques.domain.Imagen;
import com.reneseses.empaques.domain.Planilla;
import com.reneseses.empaques.domain.Turno;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;
import com.reneseses.empaques.domain.service.ImagenServiceImpl;
import com.reneseses.empaques.domain.service.PlanillaServiceImpl;
import com.reneseses.empaques.domain.service.SupermercadoServiceImpl;
import com.reneseses.empaques.domain.service.UsuarioServiceImpl;
import com.reneseses.empaques.enums.BloqueEnum;
import com.reneseses.empaques.enums.DiasEnum;
import com.reneseses.empaques.enums.RegimenTurnoEnum;
import com.reneseses.empaques.enums.TipoFaltaEnum;
import com.reneseses.empaques.enums.TipoUsuarioEnum;
import com.reneseses.empaques.formularios.ChangePasswordForm;
import com.reneseses.empaques.formularios.ExcelForm;
import com.reneseses.empaques.formularios.ForgotPasswordForm;
import com.reneseses.empaques.formularios.ImagenForm;
import com.reneseses.empaques.formularios.UsuarioCreateForm;
import com.reneseses.empaques.formularios.UsuarioUpdateForm;

@RequestMapping("/member/usuarios")
@Controller
@RooWebScaffold(path = "member/usuarios", formBackingObject = Usuario.class)
public class UsuarioController {

	@Autowired
	private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

	@Autowired
	private ChangePasswordValidator validator;

	@Autowired
	private PlanillaServiceImpl planillaServiceImpl;

	@Autowired
	private UsuarioServiceImpl usuarioServiceImpl;

	@Autowired
	private ImagenServiceImpl imagenServiceImpl;

	@Autowired
	private SupermercadoServiceImpl supermercadoServiceImpl;

	@InitBinder
	protected void initBinder(HttpServletRequest httpServletRequest, ServletRequestDataBinder binder) throws ServletException {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	@RequestMapping("turnos/solicitar")
	public String solicitud(Model uiModel) {
		uiModel.addAttribute("dias", DiasEnum.values());
		uiModel.addAttribute("bloques", BloqueEnum.values());
		return "member/usuarios/turnos/solicitud";
	}

	@RequestMapping("/getTurnos/{id}")
	public @ResponseBody ResponseEntity<String> getTurnos(@PathVariable("id") ObjectId id) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		BasicDBList response= new BasicDBList();

		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		try{
			BasicDBObject jo;
			Planilla planilla = planillaServiceImpl.findPlanilla(id);
			List<Bloque> bloques = planilla.getBloques();
			for (Bloque bloque : bloques) {
				List<Turno> turnos = bloque.getTurnos();
				for (Turno turno : turnos) {
					Integer usuario = turno.getUsuario();
					if (usuario != null && usuario == principal.getId().getNumero()) {
						jo = new BasicDBObject();
						Calendar calendar= Calendar.getInstance();
						calendar.setTime(bloque.getFecha());
						jo.put("dia", calendar.get(Calendar.DAY_OF_WEEK));
						jo.put("hora", calendar.get(Calendar.HOUR_OF_DAY));
						jo.put("minuto", calendar.get(Calendar.MINUTE));
						jo.put("diaMes", calendar.get(Calendar.DAY_OF_MONTH));
						jo.put("mes", calendar.get(Calendar.MONTH));
						jo.put("year", calendar.get(Calendar.YEAR));
						response.add(jo);
						break;
					}
				}
			}
			return new ResponseEntity<String>(response.toString(), headers, HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping("/profile")
	public @ResponseBody ResponseEntity<String> profile() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		BasicDBObject response= new BasicDBObject();

		BasicDBObject jo;
		BasicDBList planillas = new BasicDBList();
		SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
		try{
			List<Planilla> plas = planillaServiceImpl.findAllPlanillasOrderByFechaDesc(8);
			for (Planilla pla : plas) {
				jo = new BasicDBObject();
				jo.put("fecha", sdf.format(pla.getFecha()));
				jo.put("id", pla.getId().toString());
				planillas.add(jo);
			}

			jo= new BasicDBObject();
			for (int i = 0; i < BloqueEnum.values().length; i++)
				jo.put(BloqueEnum.values()[i].toString(), BloqueEnum.values()[i].getBloque());

			response.put("bloques", jo);

			jo = new BasicDBObject();
			for (int i = 0; i < DiasEnum.values().length; i++)
				jo.put(DiasEnum.values()[i].toString(), DiasEnum.values()[i].getDia());

			response.put("dias", jo);

			response.put("planillas", planillas);
			return new ResponseEntity<String>(response.toString(), headers, HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping("turnos/solicitudes")
	public String solicitudes(Model uiModel) {
		populateEditForm(uiModel, new Usuario());
		return "member/usuarios/turnos/solicitudlist";
	}

	@RequestMapping("upload")
	public String upload(Model uiModel) {
		ArrayList<TipoUsuarioEnum> array = new ArrayList<TipoUsuarioEnum>();
		array.add(TipoUsuarioEnum.EMPAQUE);
		array.add(TipoUsuarioEnum.ENCARGADO);
		uiModel.addAttribute("excel", new ExcelForm());
		uiModel.addAttribute("tipos", array);
		return "member/usuarios/upload";
	}

	@RequestMapping("uploadList")
	public String uploadUsuarios(@Valid ExcelForm excel, BindingResult bindingResult, HttpServletRequest httpServletRequest) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		InputStream inp = new ByteArrayInputStream(excel.getContent());
		Usuario usuario;
		Cell cell;
		try {
			Workbook wb = WorkbookFactory.create(inp);
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			Sheet thisSheet = wb.getSheetAt(0);
			BasicDBObject doc = new BasicDBObject();
			for (int i = 0; i <= thisSheet.getLastRowNum(); i++) {
				usuario = new Usuario();
				Row row = thisSheet.getRow(i);
				if (row != null) {
					for (int j = 0; j < 8; j++) {
						cell = row.getCell(j);
						if (cell != null) {
							int type = evaluator.evaluateInCell(cell).getCellType();
							cellContent(cell, type, doc, String.valueOf(j));
						} else {
							doc.put(String.valueOf(j), "");
						}
					}
				} else break;
				Integer numero = Integer.valueOf(doc.getInt("0"));
				usuario.setTipo(excel.getTipo());

				UsuarioId id= new UsuarioId();
				id.setNumero(numero);
				id.setSupermercado(principal.getId().getSupermercado());

				usuario.setId(id);
				usuario.setNombre(doc.getString("2") + " " + doc.getString("1"));
				String rut = doc.getString("3");
				usuario.setRut(rut);
				usuario.setPassword(messageDigestPasswordEncoder.encodePassword(usuario.getRut().substring(0, 4), null));
				if (doc.getString("4") != "") {
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					try {
						usuario.setFechaNacimiento(formatter.parse(doc.getString("4")));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				usuario.setCarrera(doc.getString("5"));
				usuario.setUniversidad(doc.getString("6"));
				usuario.setEmail(doc.getString("7"));
				usuarioService.saveUsuario(usuario);
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/member/usuarios?page=1&size=25";
	}

	private void cellContent(Cell cell, int type, BasicDBObject doc, String column) {
		switch(type) {
		case Cell.CELL_TYPE_STRING:
			doc.put(column, cell.getRichStringCellValue().getString());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				DateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
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

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updatePassword(@Valid ChangePasswordForm form, BindingResult result, Model uiModel) {
		validator.validate(form, result);
		if (result.hasErrors()) {
			uiModel.addAttribute("passForm", new ChangePasswordForm());
			return "member/usuarios/password";
		} else {
			if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
				Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				Usuario person = usuarioService.findUsuario(principal.getId());
				String newPassword = form.getNewPassword();
				person.setPassword(messageDigestPasswordEncoder.encodePassword(newPassword, null));
				usuarioService.saveUsuario(person);
				return "gracias";
			} else {
				return "login";
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(@ModelAttribute("usuario") @Valid UsuarioCreateForm usuarioForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, usuarioForm);
			return "member/usuarios/create";
		}

		UsuarioId id= new UsuarioId();
		id.setNumero(usuarioForm.getNumero());

		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			id.setSupermercado(new ObjectId(usuarioForm.getSupermercado()));
		}else{
			id.setSupermercado(principal.getId().getSupermercado());
		}

		Usuario prev= usuarioServiceImpl.findUsuario(id);

		if(prev != null){
			ObjectError error= new ObjectError("numero", "usuario con este numero ya existente");
			bindingResult.addError(error);
			populateEditForm(uiModel, usuarioForm);
			return "member/usuarios/create";
		}

		Usuario usuario = new Usuario();
		usuario.setId(id);
		usuario.setCelular(usuarioForm.getCelular());
		usuario.setDisabled(usuarioForm.getDisabled());
		usuario.setLocked(usuarioForm.getLocked());
		usuario.setNombre(usuarioForm.getNombre());
		usuario.setRut(usuarioForm.getRut());
		usuario.setEmail(usuarioForm.getEmail());
		usuario.setRegimen(usuarioForm.getRegimen());
		usuario.setTipo(usuarioForm.getTipo());
		usuario.setPrioridad(usuarioForm.getPrioridad());
		usuario.setCarrera(usuarioForm.getCarrera());
		usuario.setUniversidad(usuarioForm.getUniversidad());
		usuario.setFechaNacimiento(usuarioForm.getFechaNacimiento());
		usuario.setPassword(messageDigestPasswordEncoder.encodePassword(usuarioForm.getRut().substring(0, 4), null));
		uiModel.asMap().clear();
		usuarioService.saveUsuario(usuario);
		return "redirect:/member/usuarios/" + encodeUrlPathSegment(usuario.getId().toString(), httpServletRequest);
	}

	@RequestMapping(params = "form", produces = "text/html")
	public String createForm(Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))))
			return "accessFailure";
		populateEditForm(uiModel, new UsuarioCreateForm());
		return "member/usuarios/create";
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") UsuarioId usuarioId, Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))
				|| principal.getId().equals(usuarioId)) {
			addDateTimeFormatPatterns(uiModel);
			uiModel.addAttribute("usuario", usuarioService.findUsuario(usuarioId));
			uiModel.addAttribute("itemId", usuarioId);
			uiModel.addAttribute("passForm", new ChangePasswordForm());
			return "member/usuarios/show";
		}
		return "member/usuarios/show";
	}

	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))))
			return "accessFailure";
		if (page != null || size != null) {
			int sizeNo = size == null ? 25 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("usuarios", usuarioServiceImpl.findUsuarioEntriesOrderByNumero(firstResult, sizeNo));
			float nrOfPages = (float) usuarioService.countAllUsuarios() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("usuarios", usuarioServiceImpl.findAllOrderByNumero());
		}
		addDateTimeFormatPatterns(uiModel);
		return "member/usuarios/list";
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@ModelAttribute("usuario") @Valid UsuarioUpdateForm usuarioForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, usuarioForm);
			return "member/usuarios/update";
		}
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = usuarioService.findUsuario(usuarioForm.getId());
		
		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
			
			ObjectId supermercado=null;
			try{
				supermercado= new ObjectId(usuarioForm.getSupermercado());
			}catch(Exception e){
				bindingResult.addError(new FieldError("usuario", "supermercado", "Supermercado inválido"));
				populateEditForm(uiModel, usuarioForm);
				return "member/usuarios/update";
			}
			
			if(!usuarioForm.getNumero().equals(usuario.getId().getNumero()) || !supermercado.equals(usuario.getId().getSupermercado())){
				UsuarioId id= new UsuarioId();
				
				id.setNumero(usuarioForm.getNumero());
				id.setSupermercado(supermercado);
				
				if(usuarioServiceImpl.findUsuario(id) != null){
					bindingResult.addError(new FieldError("usuario", "numero", "Numero ya existente"));
					populateEditForm(uiModel, usuarioForm);
					return "member/usuarios/update";
				}
				
				usuarioService.deleteUsuario(usuario);
				
				usuario.setId(id);
			}
		}
		
		if ((principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))) {
			
			usuario.setDisabled(usuarioForm.getDisabled());
			usuario.setLocked(usuarioForm.getLocked());
			usuario.setRegimen(usuarioForm.getRegimen());
			usuario.setTipo(usuarioForm.getTipo());
			usuario.setPrioridad(usuarioForm.getPrioridad());
		}
		usuario.setNombre(usuarioForm.getNombre());
		usuario.setRut(usuarioForm.getRut());
		usuario.setCelular(usuarioForm.getCelular());
		usuario.setEmail(usuarioForm.getEmail());
		usuario.setCarrera(usuarioForm.getCarrera());
		usuario.setUniversidad(usuarioForm.getUniversidad());
		usuario.setFechaNacimiento(usuarioForm.getFechaNacimiento());
		uiModel.asMap().clear();
		usuarioService.saveUsuario(usuario);
		return "redirect:/member/usuarios/" + encodeUrlPathSegment(usuario.getId().toString(), httpServletRequest);
	}

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") UsuarioId id, Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || principal.getId().equals(id)) {

			Usuario usuario = usuarioService.findUsuario(id);
			UsuarioUpdateForm usuarioForm = new UsuarioUpdateForm();

			usuarioForm.setCelular(usuario.getCelular());
			usuarioForm.setDisabled(usuario.getDisabled());
			usuarioForm.setLocked(usuario.getLocked());
			usuarioForm.setNombre(usuario.getNombre());
			usuarioForm.setRut(usuario.getRut());
			usuarioForm.setNumero(usuario.getId().getNumero());
			usuarioForm.setSupermercado(usuario.getId().getSupermercado().toString());
			usuarioForm.setRegimen(usuario.getRegimen());
			usuarioForm.setId(usuario.getId());
			usuarioForm.setTipo(usuario.getTipo());
			usuarioForm.setPrioridad(usuario.getPrioridad());
			usuarioForm.setEmail(usuario.getEmail());
			usuarioForm.setCarrera(usuario.getCarrera());
			usuarioForm.setUniversidad(usuario.getUniversidad());
			usuarioForm.setFechaNacimiento(usuario.getFechaNacimiento());
			populateEditForm(uiModel, usuarioForm);
			return "member/usuarios/update";
		}
		return "accessFailure";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") UsuarioId id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))))
			return "accessFailure";

		Usuario usuario = usuarioService.findUsuario(id);
		usuarioService.deleteUsuario(usuario);
		uiModel.asMap().clear();
		uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
		uiModel.addAttribute("size", (size == null) ? "25" : size.toString());
		return "redirect:/member/usuarios?page=1&size=25";
	}

	private void populateEditForm(Model uiModel, UsuarioCreateForm usuario) {
		uiModel.addAttribute("usuario", usuario);
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("regimenturnoenums", Arrays.asList(RegimenTurnoEnum.values()));
		uiModel.addAttribute("tipousuarioenums", Arrays.asList(TipoUsuarioEnum.values()));
		uiModel.addAttribute("tipofaltaenums", Arrays.asList(TipoFaltaEnum.values()));

		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			uiModel.addAttribute("supermercados", supermercadoServiceImpl.findAllSupermercadoes());
		}
	}

	private void populateEditForm(Model uiModel, UsuarioUpdateForm usuario) {
		uiModel.addAttribute("usuario", usuario);
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("regimenturnoenums", Arrays.asList(RegimenTurnoEnum.values()));
		uiModel.addAttribute("tipousuarioenums", Arrays.asList(TipoUsuarioEnum.values()));
		uiModel.addAttribute("tipofaltaenums", Arrays.asList(TipoFaltaEnum.values()));

		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			uiModel.addAttribute("supermercados", supermercadoServiceImpl.findAllSupermercadoes());
		}
	}

	@RequestMapping(value = "imagen/create", method = RequestMethod.POST)
	public String saveImg(@Valid ImagenForm imagenForm, BindingResult result, Model model, @RequestParam("content") MultipartFile content, HttpServletRequest request) {
		if (result.hasErrors() || content.getSize() > 1050000) {
			model.addAttribute("imagen", imagenForm);
			return "member/usuarios/uploadImg";
		}
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        
		Imagen imagen = new Imagen();
		imagen.setContentType("jpg");
		imagen.setSize(content.getSize());
		imagen.setUsuario(principal.getId());
		imagen.generateImagen(imagenForm.getContent());
		imagen.setNombre(content.getOriginalFilename());
		imagen.generateThumb();
		imagenServiceImpl.saveImagen(imagen);

		return "redirect:/member/usuarios/" + principal.getId();
	}

	@RequestMapping(value = "imagen/{id}", params = "form")
	public String uploadImg(Model uiModel, @PathVariable("id") ObjectId id) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Imagen> imagenes = imagenServiceImpl.findImagenByUsuario(principal.getId());
		if (!principal.getId().equals(id) || imagenes.size() > 5) {
			return "accessFailure";
		}
		uiModel.addAttribute("imagen", new ImagenForm());
		uiModel.addAttribute("usuario", id);
		return "member/usuarios/uploadImg";
	}

	@RequestMapping(value = "imagen", params = "get")
	public String getImagen(@RequestParam("get") ObjectId id, HttpServletResponse response) {
		Imagen img = imagenServiceImpl.findImagen(id);
		try {
			response.setHeader("Content-Disposition", "inline;filename=\"" + img.getNombre() + ".jpg" + "\"");
			OutputStream out = response.getOutputStream();
			response.setContentType("jpg");
			IOUtils.copy(new ByteArrayInputStream(img.getContent()), out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "imagen", params = "thumb")
	public String getThumb(@RequestParam("thumb") ObjectId id, HttpServletResponse response) {
		Imagen img = imagenServiceImpl.findImagen(id);
		try {
			response.setHeader("Content-Disposition", "inline;filename=\"" + img.getNombre() + "thumb" + ".jpg" + "\"");
			OutputStream out = response.getOutputStream();
			response.setContentType("jpg");
			IOUtils.copy(new ByteArrayInputStream(img.getThumbnail()), out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "imagen", params = "sel")
	public String select(@RequestParam("sel") ObjectId id, HttpServletResponse response) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Imagen img = imagenServiceImpl.findImagen(id);
		if (!principal.getId().equals(img.getUsuario()))
			return "accessFailure";
		Usuario user = usuarioService.findUsuario(principal.getId());

		user.setImage(img.getId());

		usuarioServiceImpl.saveUsuario(user);

		return "redirect:/member/usuarios/" + principal.getId();
	}

	@RequestMapping(value = "imagen")
	public @ResponseBody String userImg(HttpServletResponse response) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Usuario usuario= usuarioServiceImpl.findUsuario(principal.getId());

		return usuario.getImage()== null? null: usuario.getImage().toString();
	}

	@RequestMapping(value = "imagen/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String deleteImagen(@PathVariable("id") ObjectId id, Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario user = usuarioService.findUsuario(principal.getId());
		Imagen imagen = imagenServiceImpl.findImagen(id);

		if (imagen.getId().equals(user.getImage())) {
			user.setImage(null);
			usuarioServiceImpl.saveUsuario(user);
		}
		imagenServiceImpl.deleteImagen(imagen);
		return "redirect:/member/usuarios/imagen/" + String.valueOf(principal.getId());
	}

	@RequestMapping("imagen/{id}")
	public String imagenes(Model uiModel, @PathVariable("id") Integer id) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UsuarioId usuarioId= new UsuarioId();
		usuarioId.setNumero(id);
		usuarioId.setSupermercado(principal.getId().getSupermercado());

		List<Imagen> imagenes= imagenServiceImpl.findImagenByUsuario(usuarioId);
		if (imagenes.size() == 0)
			return "redirect:/member/usuarios/imagen/" + String.valueOf(id) + "?form";

		uiModel.addAttribute("imagenes", imagenes);
		uiModel.addAttribute("user", id);
		return "member/usuarios/imagen";
	}

	@RequestMapping("/resetpass")
	public String resetPasswordForm(Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))
				|| principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))))
			return "accessFailure";
		uiModel.addAttribute("passForm", new ForgotPasswordForm());
		return "member/usuarios/reset";
	}

	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public String resetPassword(@Valid ForgotPasswordForm form, BindingResult result, Model uiModel) {
		if (result.hasErrors()) {
			uiModel.addAttribute("passForm", form);
			return "member/usuarios/reset";
		} else {
			Usuario usuario = usuarioServiceImpl.findUsuarioByRut(form.getRut());
			if (usuario == null) {
				uiModel.addAttribute("passForm", form);
				uiModel.addAttribute("error", "Usuario no encontrado");
				return "member/usuarios/reset";
			}
			if (!usuario.getId().getNumero().equals(new Integer(form.getNumero()))) {
				uiModel.addAttribute("passForm", form);
				uiModel.addAttribute("error", "Usuario no encontrado");
				return "member/usuarios/reset";
			}
			usuario.setPassword(messageDigestPasswordEncoder.encodePassword(usuario.getRut().substring(0, 4), null));
			usuarioService.saveUsuario(usuario);
			return "gracias";
		}
	}

	@RequestMapping(value="find", method = RequestMethod.GET)
	public String findUsuarioForm(Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			uiModel.addAttribute("supermercados", supermercadoServiceImpl.findAllSupermercadoes());
		}
		
		return "member/usuarios/find";
	}

	@RequestMapping(params = { "find=ByNumeroEquals", "form" }, method = RequestMethod.GET)
	public String findUsuariosByNumeroEqualsForm(Model uiModel) {
		return "member/usuarios/findUsuariosByNumeroEquals";
	}

	@RequestMapping(params = "find=ByNumeroEquals", method = RequestMethod.GET)
	public String findUsuariosByNumeroEquals(@RequestParam("numero") Integer numero, @RequestParam(value="supermercado", required=false) ObjectId supermercado, Model uiModel) {
		List<Usuario> list = new ArrayList<Usuario>();

		UsuarioId id= new UsuarioId();
		id.setNumero(numero);

		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			id.setSupermercado(supermercado);
		}else{
			id.setSupermercado(principal.getId().getSupermercado());
		}

		Usuario user= usuarioServiceImpl.findUsuario(id);
		if(user != null)
			list.add(user);

		uiModel.addAttribute("usuarios", list);
		return "member/usuarios/list";
	}

	@RequestMapping(params = { "find=ByRutEquals", "form" }, method = RequestMethod.GET)
	public String findUsuariosByRutEqualsForm(Model uiModel) {
		return "member/usuarios/findUsuariosByRutEquals";
	}

	@RequestMapping(params = "find=ByRutEquals", method = RequestMethod.GET)
	public String findUsuariosByRutEquals(@RequestParam("rut") String rut, Model uiModel) {
		List<Usuario> list = new ArrayList<Usuario>();

		Usuario user= usuarioServiceImpl.findUsuarioByRut(rut);
		if(user != null)
			list.add(user);

		uiModel.addAttribute("usuarios", list);
		return "member/usuarios/list";
	}
}
