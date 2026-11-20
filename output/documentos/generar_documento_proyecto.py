from pathlib import Path
from datetime import date
from docx import Document
from docx.shared import Cm, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.section import WD_SECTION
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent
OUT = ROOT / "Proyecto_Final_Sistema_de_Inventarios.docx"
ASSETS = ROOT / "recursos"
ASSETS.mkdir(exist_ok=True)

BLUE = "17365D"
LIGHT_BLUE = "D9EAF7"
GRAY = "F2F2F2"
GREEN = "E2F0D9"
YELLOW = "FFF2CC"
RED = "FCE4D6"
BLUE_IMG = "#" + BLUE
LIGHT_BLUE_IMG = "#" + LIGHT_BLUE

def font(size, bold=False):
    try:
        return ImageFont.truetype("C:/Windows/Fonts/arialbd.ttf" if bold else "C:/Windows/Fonts/arial.ttf", size)
    except OSError:
        return ImageFont.load_default()

def draw_box(draw, xy, title, lines, fill, title_fill=None):
    x1, y1, x2, y2 = xy
    image_fill = "#" + fill if len(fill) == 6 else fill
    draw.rounded_rectangle(xy, radius=12, fill=image_fill, outline=BLUE_IMG, width=2)
    draw.rounded_rectangle((x1, y1, x2, y1 + 35), radius=12, fill=title_fill or BLUE_IMG, outline=BLUE_IMG, width=2)
    draw.text((x1 + 12, y1 + 7), title, font=font(17, True), fill="#FFFFFF")
    y = y1 + 48
    for line in lines:
        draw.text((x1 + 14, y), line, font=font(14), fill="#1F1F1F")
        y += 22

def arrow(draw, start, end, text=None):
    draw.line((start, end), fill=BLUE_IMG, width=3)
    draw.polygon([(end[0], end[1]), (end[0]-10, end[1]-6), (end[0]-10, end[1]+6)], fill=BLUE_IMG)
    if text:
        x = (start[0] + end[0]) // 2
        y = (start[1] + end[1]) // 2 - 20
        draw.text((x-35, y), text, font=font(13), fill=BLUE_IMG)

def make_use_cases():
    p = ASSETS / "diagrama_casos_uso.png"
    im = Image.new("RGB", (1400, 880), "WHITE")
    d = ImageDraw.Draw(im)
    d.text((480, 25), "Diagrama de casos de uso", font=font(30, True), fill=BLUE_IMG)
    # actor
    d.ellipse((85, 305, 135, 355), outline=BLUE_IMG, width=4)
    d.line((110,355,110,455), fill=BLUE_IMG, width=4); d.line((65,395,155,395), fill=BLUE_IMG, width=4)
    d.line((110,455,65,520), fill=BLUE_IMG, width=4); d.line((110,455,155,520), fill=BLUE_IMG, width=4)
    d.text((47,545), "Administrador", font=font(20, True), fill=BLUE_IMG)
    d.rectangle((310, 100, 1330, 770), outline=BLUE_IMG, width=3)
    d.text((605, 112), "Sistema de Inventarios", font=font(22, True), fill=BLUE_IMG)
    cases = [(470,210,"Iniciar sesión"),(850,210,"Gestionar productos"),(1100,210,"Gestionar proveedores"),
             (470,500,"Consultar inventario"),(850,500,"Calcular precio"),(1100,500,"Cerrar sesión")]
    for x,y,label in cases:
        d.ellipse((x-145,y-45,x+145,y+45), fill=LIGHT_BLUE_IMG, outline=BLUE_IMG, width=3)
        bbox = d.textbbox((0,0), label, font=font(17, True))
        d.text((x-(bbox[2]-bbox[0])/2,y-10), label, font=font(17, True), fill="#1F1F1F")
        d.line((155,395,x-145,y), fill="#606060", width=2)
    im.save(p)
    return p

def make_class_diagram():
    p = ASSETS / "diagrama_clases.png"
    im = Image.new("RGB", (1500, 920), "WHITE")
    d = ImageDraw.Draw(im)
    d.text((545, 20), "Diagrama de clases simplificado", font=font(30, True), fill=BLUE_IMG)
    draw_box(d,(80,150,450,390),"Modelo",["Usuario", "Producto", "Proveedor", "Categoría", "Cliente, Compra, Venta", "DetalleCompra / DetalleVenta"],"EAF2F8")
    draw_box(d,(565,150,945,430),"Acceso a datos",["DaoGenerico<T>", "ProductoDAO / ProveedorDAO", "UsuarioDAO", "...DAOImpl", "ConexionBaseDatos"],"E2F0D9")
    draw_box(d,(1050,150,1425,430),"Controladores",["LoginController", "MenuPrincipalController", "NuevoProductoController", "EditarProductoController", "ProveedoresController"],"FFF2CC")
    draw_box(d,(365,570,730,790),"Vistas JavaFX",["Login.fxml", "MenuPrincipal.fxml", "NuevoProducto.fxml", "EditarProducto.fxml", "Proveedores.fxml"],"FCE4D6")
    draw_box(d,(930,570,1285,790),"Estrategias",["PrecioStrategy", "PrecioLocalStrategy", "PrecioAPIStrategy"],"EDE7F6")
    arrow(d,(450,270),(565,270),"CRUD")
    arrow(d,(945,290),(1050,290),"datos")
    arrow(d,(1050,430),(730,620),"eventos")
    arrow(d,(730,680),(930,680),"precio")
    im.save(p)
    return p

def make_er_diagram():
    p = ASSETS / "diagrama_bd.png"
    im = Image.new("RGB", (1600, 1040), "WHITE")
    d = ImageDraw.Draw(im)
    d.text((520, 18), "Modelo relacional normalizado", font=font(30, True), fill=BLUE_IMG)
    boxes = {
        "usuarios": (85,120,330,230), "categorias": (85,360,330,470), "proveedores": (85,610,330,720),
        "productos": (570,335,865,490), "clientes": (570,700,815,810),
        "compras": (1080,120,1325,230), "detalle_compras": (1325,260,1570,380),
        "ventas": (1080,580,1325,700), "detalle_ventas": (1325,730,1570,850), "movimientos": (940,825,1205,950)
    }
    fields = {
        "usuarios":["PK id_usuario", "username (UQ)", "password, rol"], "categorias":["PK id_categoria", "nombre (UQ)"],
        "proveedores":["PK id_proveedor", "nombre, contacto"], "productos":["PK id_producto", "FK id_categoria", "FK id_proveedor", "codigo (UQ), stock"],
        "clientes":["PK id_cliente", "nombre, documento"], "compras":["PK id_compra", "FK id_proveedor"],
        "detalle_compras":["PK2 id_compra + id_producto", "FK2 compra / producto"], "ventas":["PK id_venta", "FK id_cliente"],
        "detalle_ventas":["PK2 id_venta + id_producto", "FK2 venta / producto"], "movimientos":["PK id_movimiento", "FK id_producto", "FK id_usuario"]
    }
    for name, xy in boxes.items(): draw_box(d,xy,name,fields[name],"F8FBFD")
    links=[("categorias","productos"),("proveedores","productos"),("proveedores","compras"),("compras","detalle_compras"),("productos","detalle_compras"),("clientes","ventas"),("ventas","detalle_ventas"),("productos","detalle_ventas"),("productos","movimientos"),("usuarios","movimientos")]
    for a,b in links:
        ax=(boxes[a][0]+boxes[a][2])//2; ay=(boxes[a][1]+boxes[a][3])//2
        bx=(boxes[b][0]+boxes[b][2])//2; by=(boxes[b][1]+boxes[b][3])//2
        d.line((ax,ay,bx,by),fill="#7F8C8D",width=2)
    im.save(p)
    return p

def make_sequence_diagram():
    p = ASSETS / "diagrama_secuencia_login.png"
    im = Image.new("RGB", (1400, 790), "WHITE")
    d = ImageDraw.Draw(im)
    d.text((500,20), "Secuencia: autenticación", font=font(30, True), fill=BLUE_IMG)
    names=["Usuario","Login.fxml","LoginController","UsuarioDAO","MySQL"]
    xs=[140,390,680,970,1240]
    for x,name in zip(xs,names):
        d.rectangle((x-80,85,x+80,130),fill=LIGHT_BLUE_IMG,outline=BLUE_IMG,width=2)
        bb=d.textbbox((0,0),name,font=font(15,True));d.text((x-(bb[2]-bb[0])/2,99),name,font=font(15,True),fill="#1F1F1F")
        d.line((x,130,x,720),fill="#9E9E9E",width=2)
    steps=[(0,1,"Ingresa credenciales"),(1,2,"manejarIngreso()"),(2,3,"autenticar(usuario, SHA-1)"),(3,4,"SELECT usuario activo"),(4,3,"resultado"),(3,2,"Usuario / null"),(2,1,"alerta y menú / error")]
    y=190
    for a,b,label in steps:
        arrow(d,(xs[a],y),(xs[b],y),label)
        y+=70
    im.save(p)
    return p

def shade(cell, color):
    tcPr = cell._tc.get_or_add_tcPr(); shd = OxmlElement('w:shd'); shd.set(qn('w:fill'), color); tcPr.append(shd)

def cell_text(cell, text, bold=False, color=None):
    cell.text = ""
    p = cell.paragraphs[0]; p.paragraph_format.space_after = Pt(0)
    r = p.add_run(str(text)); r.bold = bold; r.font.size = Pt(9); r.font.name = "Arial"
    if color: r.font.color.rgb = RGBColor.from_string(color)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER

def add_table(doc, headers, rows, widths=None):
    t = doc.add_table(rows=1, cols=len(headers)); t.style = "Table Grid"; t.alignment = WD_TABLE_ALIGNMENT.CENTER
    for i,h in enumerate(headers):
        cell_text(t.rows[0].cells[i], h, True, "FFFFFF"); shade(t.rows[0].cells[i], BLUE)
    for ri,row in enumerate(rows):
        cells=t.add_row().cells
        for i,val in enumerate(row):
            cell_text(cells[i], val)
            if ri % 2 == 1: shade(cells[i], GRAY)
    if widths:
        for row in t.rows:
            for i,w in enumerate(widths): row.cells[i].width = Cm(w)
    doc.add_paragraph()
    return t

def add_heading(doc, text, level=1):
    h=doc.add_heading(text, level=level)
    for r in h.runs:
        r.font.name="Arial"; r.font.color.rgb=RGBColor.from_string(BLUE)
    return h

def add_p(doc, text="", bold_prefix=None):
    p=doc.add_paragraph(); p.paragraph_format.space_after=Pt(7); p.paragraph_format.line_spacing=1.15
    if bold_prefix and text.startswith(bold_prefix):
        r=p.add_run(bold_prefix);r.bold=True;r.font.name="Arial";r.font.size=Pt(10.5)
        r=p.add_run(text[len(bold_prefix):]);r.font.name="Arial";r.font.size=Pt(10.5)
    else:
        r=p.add_run(text);r.font.name="Arial";r.font.size=Pt(10.5)
    return p

def add_bullets(doc, items):
    for text in items:
        p=doc.add_paragraph(style="List Bullet"); p.paragraph_format.space_after=Pt(4)
        r=p.add_run(text);r.font.name="Arial";r.font.size=Pt(10.5)

def add_status(doc, status):
    color = GREEN if status == "Implementado" else YELLOW if status == "Parcial" else RED
    p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.RIGHT
    r=p.add_run(status.upper()); r.bold=True; r.font.size=Pt(9); r.font.color.rgb=RGBColor.from_string(BLUE)
    p._p.get_or_add_pPr().append(OxmlElement('w:keepNext'))

def header_footer(section):
    header=section.header.paragraphs[0]; header.alignment=WD_ALIGN_PARAGRAPH.RIGHT
    r=header.add_run("Sistema de Inventarios | Proyecto Final");r.font.name="Arial";r.font.size=Pt(8);r.font.color.rgb=RGBColor.from_string("6B6B6B")
    footer=section.footer.paragraphs[0];footer.alignment=WD_ALIGN_PARAGRAPH.CENTER
    r=footer.add_run("Tópicos Avanzados de Programación | Documento editable");r.font.name="Arial";r.font.size=Pt(8);r.font.color.rgb=RGBColor.from_string("6B6B6B")

def main():
    use_case=make_use_cases(); class_diagram=make_class_diagram(); er=make_er_diagram(); sequence=make_sequence_diagram()
    doc=Document()
    sec=doc.sections[0];sec.top_margin=Cm(2.2);sec.bottom_margin=Cm(2.0);sec.left_margin=Cm(2.3);sec.right_margin=Cm(2.3)
    header_footer(sec)
    styles=doc.styles
    styles['Normal'].font.name='Arial';styles['Normal']._element.rPr.rFonts.set(qn('w:eastAsia'),'Arial')

    # Cover
    for _ in range(4): doc.add_paragraph()
    p=doc.add_paragraph();p.alignment=WD_ALIGN_PARAGRAPH.CENTER
    r=p.add_run("[NOMBRE DE LA INSTITUCIÓN]");r.bold=True;r.font.name="Arial";r.font.size=Pt(17);r.font.color.rgb=RGBColor.from_string(BLUE)
    p=doc.add_paragraph();p.alignment=WD_ALIGN_PARAGRAPH.CENTER;r=p.add_run("Tópicos Avanzados de Programación");r.font.name="Arial";r.font.size=Pt(14)
    for _ in range(4): doc.add_paragraph()
    p=doc.add_paragraph();p.alignment=WD_ALIGN_PARAGRAPH.CENTER;r=p.add_run("PROYECTO FINAL");r.bold=True;r.font.name="Arial";r.font.size=Pt(23);r.font.color.rgb=RGBColor.from_string(BLUE)
    p=doc.add_paragraph();p.alignment=WD_ALIGN_PARAGRAPH.CENTER;r=p.add_run("Sistema de Gestión de Inventarios");r.bold=True;r.font.name="Arial";r.font.size=Pt(27)
    p=doc.add_paragraph();p.alignment=WD_ALIGN_PARAGRAPH.CENTER;r=p.add_run("Documento de análisis, diseño, implementación y pruebas");r.italic=True;r.font.name="Arial";r.font.size=Pt(13)
    for _ in range(6): doc.add_paragraph()
    for label,value in [("Alumno:","Oskar Luis Cabrera Chavero"),("Docente:","[Nombre del docente]"),("Semestre y grupo:","[Completar]"),("Fecha:","25 de octubre de 2026")]:
        p=doc.add_paragraph();p.alignment=WD_ALIGN_PARAGRAPH.CENTER
        r=p.add_run(label+" ");r.bold=True;r.font.name="Arial";r.font.size=Pt(11)
        r=p.add_run(value);r.font.name="Arial";r.font.size=Pt(11)
    doc.add_page_break()

    add_heading(doc,"Índice",1)
    add_table(doc,["Sección","Contenido"],[
        ("1","Resumen ejecutivo y objetivos"),("2","Análisis funcional"),("3","Requerimientos del proyecto"),("4","Casos de uso"),("5","Diseño y arquitectura"),("6","Base de datos normalizada"),("7","Implementación y evidencias"),("8","Pruebas, instalación y guías"),("9","Conclusiones y trabajo pendiente")], [2,14])
    add_p(doc,"Nota: los campos entre corchetes son editables y deben completarse antes de la entrega final.")
    doc.add_page_break()

    add_heading(doc,"1. Resumen ejecutivo y objetivos",1)
    add_p(doc,"El Sistema de Gestión de Inventarios es una aplicación de escritorio construida con Java y JavaFX. Su propósito es centralizar el registro y la consulta de productos y proveedores, con autenticación de usuarios y persistencia en MySQL. La solución adopta una arquitectura Modelo–Vista–Controlador (MVC), acceso a datos mediante DAO y una base de datos relacional normalizada.")
    add_heading(doc,"1.1 Problema a resolver",2)
    add_p(doc,"Un control manual de existencias puede provocar duplicidad de información, falta de trazabilidad, errores al modificar precios o cantidades y dificultad para localizar proveedores. El sistema propone una fuente única de datos para operar el inventario de forma más ordenada y segura.")
    add_heading(doc,"1.2 Objetivo general",2)
    add_p(doc,"Desarrollar una aplicación JavaFX para administrar información de inventario mediante una interfaz gráfica, autenticación con base de datos, arquitectura MVC, patrones de diseño y control de versiones con Git y GitHub.")
    add_heading(doc,"1.3 Objetivos específicos",2)
    add_bullets(doc,["Autenticar usuarios contra MySQL aplicando SHA-1 a la contraseña antes de validarla.","Gestionar productos y proveedores desde pantallas JavaFX con formularios, tablas, alertas y validaciones.","Mantener una base de datos normalizada que conserve la integridad de productos, categorías, proveedores, compras, ventas y movimientos.","Aplicar patrones de diseño y programación genérica para lograr código modular, reutilizable y mantenible."])
    add_heading(doc,"1.4 Alcance actual",2)
    add_table(doc,["Módulo","Estado","Descripción"],[
        ("Inicio de sesión","Implementado","Valida usuario y contraseña contra la tabla usuarios y muestra alertas."),
        ("Productos","Implementado","Consulta, alta, edición y eliminación; valida campos y duplicados."),
        ("Proveedores","Implementado","Consulta, alta, edición y eliminación de proveedores."),
        ("Precios","Implementado","Calcula precio mediante estrategias local o API externa con respaldo."),
        ("Compras, ventas y movimientos","Diseñados en BD","El esquema relacional está preparado; las pantallas y flujos aún no están conectados en la interfaz.")])
    doc.add_page_break()

    add_heading(doc,"2. Análisis funcional",1)
    add_heading(doc,"2.1 Justificación",2)
    add_p(doc,"El proyecto integra los contenidos de programación orientada a objetos, interfaces gráficas, conectividad JDBC, bases de datos y buenas prácticas de desarrollo. Al administrar productos y proveedores en una misma aplicación, reduce registros dispersos y prepara el sistema para operaciones futuras de compra, venta y movimiento de inventario.")
    add_heading(doc,"2.2 Rol de usuario",2)
    add_table(doc,["Rol","Responsabilidades"],[
        ("Administrador","Inicia sesión, consulta el catálogo, registra y modifica productos, administra proveedores y utiliza el cálculo de precios."),
        ("Usuario futuro","La tabla usuarios contempla roles y estado activo; se puede ampliar con permisos específicos cuando se agreguen más módulos.")])
    add_heading(doc,"2.3 Requerimientos funcionales",2)
    add_table(doc,["ID","Requerimiento","Estado"],[
        ("RF-01","Validar credenciales y permitir o negar el acceso al menú principal.","Implementado"),
        ("RF-02","Mostrar alertas comprensibles ante campos vacíos, credenciales inválidas y errores de operación.","Implementado"),
        ("RF-03","Consultar, registrar, editar y eliminar productos.","Implementado"),
        ("RF-04","Evitar códigos o nombres de producto duplicados y valores de stock negativos.","Implementado"),
        ("RF-05","Consultar, registrar, editar y eliminar proveedores.","Implementado"),
        ("RF-06","Calcular un precio con estrategias intercambiables.","Implementado"),
        ("RF-07","Registrar compras, ventas y movimientos que afecten existencias.","Pendiente de interfaz/lógica")])
    add_heading(doc,"2.4 Requerimientos no funcionales",2)
    add_table(doc,["ID","Requerimiento"],[
        ("RNF-01","La interfaz debe construirse en JavaFX y mantener navegación y mensajes consistentes."),
        ("RNF-02","La información persistente se almacena en MySQL mediante JDBC."),
        ("RNF-03","La contraseña se transforma con SHA-1 antes de enviarse a la consulta de autenticación."),
        ("RNF-04","El código debe separar modelo, vistas, controladores y acceso a datos."),
        ("RNF-05","El historial del desarrollo debe mantenerse con Git, ramas y commits descriptivos.")])
    doc.add_page_break()

    add_heading(doc,"3. Requerimientos técnicos y trazabilidad",1)
    add_p(doc,"La siguiente tabla relaciona los requisitos definidos para el proyecto final con la evidencia existente en el repositorio. El estado “parcial” señala una base implementada que requiere ampliación antes de declarar la funcionalidad completa.")
    add_table(doc,["Requisito","Evidencia del proyecto","Estado"],[
        ("Git y GitHub","Repositorio con ramas main, develop y ramas feature; historial de commits y README.md.","Implementado"),
        ("Interfaz JavaFX","Vistas FXML para login, menú, productos y proveedores; controladores y alertas.","Implementado"),
        ("Login + BD + SHA-1","LoginController, UsuarioDAOImp, UtilidadSeguridad y tabla usuarios.","Implementado"),
        ("Cuatro patrones","Singleton, DAO, Strategy y MVC/Controller como patrón arquitectónico.","Implementado"),
        ("Programación genérica","Interfaz DaoGenerico<T> para operaciones CRUD reutilizables.","Implementado"),
        ("MVC","Paquetes models, views y controllers separados.","Implementado"),
        ("Diagramas UML y BD","Diagramas de este documento y script SQL normalizado.","Implementado"),
        ("Pruebas por módulo","Compilación realizada; faltan pruebas funcionales ejecutadas contra MySQL para todos los módulos.","Parcial"),
        ("Guía de usuario y técnica","Incluidas en la sección 8; pueden complementarse con capturas de pantalla.","Implementado")])
    add_heading(doc,"3.1 Control de versiones",2)
    add_p(doc,"La rama principal concentra versiones estables; develop integra cambios; las ramas feature separan unidades de trabajo como login, arquitectura MVC y conexión de base de datos. Los commits deben describir una sola intención: por ejemplo, “feat: add supplier CRUD screen” o “fix: validate product stock before save”.")
    doc.add_page_break()

    add_heading(doc,"4. Casos de uso",1)
    add_p(doc,"Actor principal: Administrador. El sistema solicita autenticación antes de habilitar el menú. Los seis casos siguientes cubren el mínimo solicitado y describen el comportamiento visible del sistema.")
    doc.add_picture(str(use_case), width=Cm(16.5)); doc.paragraphs[-1].alignment=WD_ALIGN_PARAGRAPH.CENTER
    add_table(doc,["Caso de uso","Flujo principal","Resultado"],[
        ("CU-01 Iniciar sesión","El usuario captura credenciales; el controlador aplica SHA-1 y consulta el DAO.","Acceso al menú o alerta de error."),
        ("CU-02 Gestionar productos","El administrador consulta la tabla y abre los formularios de alta o edición.","Producto almacenado, actualizado o eliminado."),
        ("CU-03 Gestionar proveedores","El administrador consulta y modifica datos de proveedor.","Proveedor persistido en MySQL."),
        ("CU-04 Consultar inventario","El administrador revisa los productos disponibles y su stock.","Información de inventario visible."),
        ("CU-05 Calcular precio","Se selecciona una estrategia local o proveniente de API.","Precio calculado según la estrategia."),
        ("CU-06 Cerrar sesión","El usuario selecciona salir desde la aplicación.","La ventana se cierra de forma controlada.")])
    doc.add_page_break()

    add_heading(doc,"5. Diseño y arquitectura",1)
    add_heading(doc,"5.1 Arquitectura MVC",2)
    add_p(doc,"El Modelo contiene entidades como Producto, Proveedor y Usuario; las Vistas son archivos FXML construidos para JavaFX; los Controladores reciben eventos, validan datos y coordinan la navegación o el acceso a servicios. Las clases DAO concentran SQL y JDBC, evitando que la vista manipule la base de datos directamente.")
    doc.add_picture(str(class_diagram), width=Cm(16.5)); doc.paragraphs[-1].alignment=WD_ALIGN_PARAGRAPH.CENTER
    add_heading(doc,"5.2 Patrones de diseño",2)
    add_table(doc,["Patrón","Aplicación","Justificación"],[
        ("Singleton","ConexionBaseDatos","Mantiene una instancia compartida para obtener la conexión JDBC configurada."),
        ("DAO","ProductoDAOImpl, ProveedorDAOImpl, UsuarioDAOImp","Aísla sentencias SQL y operaciones CRUD del resto de la aplicación."),
        ("Strategy","PrecioStrategy, PrecioLocalStrategy, PrecioAPIStrategy","Permite cambiar la forma de calcular precios sin alterar a los consumidores."),
        ("MVC","models, views, controllers","Separa datos, interfaz y control de eventos para facilitar mantenimiento y pruebas.")])
    add_heading(doc,"5.3 Programación genérica",2)
    add_p(doc,"La interfaz DaoGenerico<T> define crear, obtenerPorId, obtenerTodos, actualizar y eliminar para cualquier entidad T. Así se evita repetir el contrato CRUD para Producto, Proveedor u otros modelos; cada DAO conserva únicamente su lógica específica de consulta.")
    doc.add_page_break()

    add_heading(doc,"6. Base de datos normalizada",1)
    add_p(doc,"El esquema sistema_inventarios separa entidades independientes para reducir duplicidad y preservar integridad referencial. Productos se relaciona con categorías y proveedores; compras y ventas se dividen en encabezado y detalle; movimientos registra cambios asociados con un producto y, de existir, un usuario.")
    doc.add_picture(str(er), width=Cm(14.5)); doc.paragraphs[-1].alignment=WD_ALIGN_PARAGRAPH.CENTER
    add_heading(doc,"6.1 Normalización aplicada",2)
    add_table(doc,["Forma normal","Aplicación en el proyecto"],[
        ("Primera forma normal (1FN)","Cada tabla tiene atributos atómicos y una clave primaria; no se almacenan listas de productos dentro de compras o ventas."),
        ("Segunda forma normal (2FN)","Los detalles de compra y venta usan claves compuestas y sus atributos cantidad y precio_unitario dependen de la combinación completa."),
        ("Tercera forma normal (3FN)","Categorías, proveedores y clientes se guardan en tablas propias; productos conserva solo las claves foráneas necesarias."),
        ("Integridad","Las constraints PK_, FK_, PK2_, FK2_, UQ_ y CK_ declaran claves, referencias, unicidad y validaciones directamente en MySQL.")])
    add_heading(doc,"6.2 Restricciones destacadas",2)
    add_bullets(doc,["PK_ identifica la llave primaria de cada tabla principal, por ejemplo PK_productos.","FK_ declara relaciones simples, por ejemplo un producto con categoría y proveedor.","PK2_ y FK2_ se usan en tablas de detalle con llave compuesta, como detalle_compras y detalle_ventas.","UQ_ evita duplicar datos críticos como username, código de producto y código de venta.","CK_ evita valores inválidos, por ejemplo stock negativo, cantidad no positiva o precios negativos."])
    doc.add_page_break()

    add_heading(doc,"7. Implementación",1)
    add_heading(doc,"7.1 Inicio de sesión",2)
    add_p(doc,"Login.fxml contiene los campos de usuario y contraseña. LoginController valida que no estén vacíos, transforma la contraseña con UtilidadSeguridad.encriptarSHA1 y delega la validación a UsuarioDAOImp. Si la autenticación es válida, carga MenuPrincipal.fxml; de lo contrario, informa el error mediante una alerta.")
    doc.add_picture(str(sequence), width=Cm(16.5)); doc.paragraphs[-1].alignment=WD_ALIGN_PARAGRAPH.CENTER
    add_heading(doc,"7.2 Productos y proveedores",2)
    add_p(doc,"Los controladores de productos y proveedores conectan formularios FXML con sus DAO correspondientes. La operación de productos valida datos básicos y protege la información ante fallos de actualización. Las tablas permiten consultar los datos existentes antes de editar o eliminar.")
    add_heading(doc,"7.3 Cálculo de precios",2)
    add_p(doc,"PrecioLocalStrategy aplica una regla local al precio base. PrecioAPIStrategy consulta una API de referencia y cuenta con una alternativa cuando el servicio no está disponible. Ambas implementan PrecioStrategy, por lo que son intercambiables sin cambiar el contrato del cálculo.")
    add_heading(doc,"7.4 Estado de módulos futuros",2)
    add_p(doc,"Las entidades y relaciones de compras, ventas, detalles, clientes y movimientos existen en el modelo y en la base de datos. Para completar el alcance total, se deben crear sus DAO concretos, controladores, vistas FXML, validaciones de stock y pruebas funcionales integradas.")
    doc.add_page_break()

    add_heading(doc,"8. Pruebas, guía de instalación y guía de usuario",1)
    add_heading(doc,"8.1 Pruebas realizadas",2)
    add_table(doc,["Prueba","Resultado","Evidencia / observación"],[
        ("Compilación del proyecto Maven","Correcta","El proyecto compila con las dependencias disponibles."),
        ("Validación de edición de productos","Revisada en código","Se validan datos y se preserva el comportamiento ante fallo de actualización."),
        ("Integración contra MySQL","Pendiente de ejecutar en entorno local","Se debe levantar MySQL, cargar database_script.sql y probar los flujos completos."),
        ("Módulos de compra/venta/movimiento","Pendiente","El esquema está listo; falta interfaz y lógica de aplicación.")])
    add_heading(doc,"8.2 Guía técnica de instalación",2)
    add_bullets(doc,["Instalar JDK 21, Maven y MySQL 8 o utilizar el entorno Docker definido por el proyecto.","Crear la base de datos ejecutando database_script.sql en MySQL Workbench o mediante el contenedor configurado.","Crear o verificar src/main/resources/database.properties con db.url, db.user y db.password de la instancia local. No incluir credenciales personales en Git.","Desde la raíz del proyecto, ejecutar mvnw compile y después mvnw javafx:run (o la configuración equivalente del IDE).", "Confirmar que el puerto, usuario y contraseña coincidan con la instancia de MySQL antes de ejecutar la aplicación."])
    add_heading(doc,"8.3 Guía rápida de usuario",2)
    add_bullets(doc,["Abrir la aplicación e ingresar con un usuario válido de la base de datos. Para la carga inicial de demostración: usuario admin y contraseña admin123; cambiar esta contraseña en un uso real.","Desde el menú principal, abrir Productos para consultar o administrar el catálogo.","Completar los campos requeridos al crear o editar un producto; el sistema mostrará alertas si detecta datos inválidos o duplicados.","Abrir Proveedores para administrar la información de quienes suministran productos.","Usar la opción de salida para cerrar la aplicación."])
    doc.add_page_break()

    add_heading(doc,"9. Conclusiones y trabajo pendiente",1)
    add_p(doc,"El proyecto ya cuenta con una base sólida: aplicación JavaFX, autenticación conectada a MySQL, contraseña validada con SHA-1, administración de productos y proveedores, estructura MVC, DAO genérico, estrategias de precio y una base de datos normalizada con restricciones nombradas. Estos elementos cubren de forma directa los requerimientos técnicos centrales del proyecto final.")
    add_p(doc,"Para cerrar el proyecto con una funcionalidad completa, la prioridad es implementar los flujos de compras, ventas y movimientos de inventario, incluyendo sus vistas, DAO concretos, actualización transaccional de stock y pruebas funcionales contra una instancia real de MySQL. También conviene añadir capturas de pantalla de las vistas terminadas y evidencias de ejecución a esta documentación.")
    add_heading(doc,"Anexo A. Archivos relevantes",2)
    add_table(doc,["Propósito","Ubicación"],[
        ("Script de esquema y datos iniciales","database_script.sql"),("Punto de entrada JavaFX","src/main/java/com/cabrera/inventario/Launcher.java"),("Controlador de inicio de sesión","src/main/java/com/cabrera/inventario/controllers/LoginController.java"),("Interfaz DAO genérica","src/main/java/com/cabrera/inventario/dao/DaoGenerico.java"),("Conexión Singleton","src/main/java/com/cabrera/inventario/util/ConexionBaseDatos.java"),("Vistas JavaFX","src/main/resources/com/cabrera/inventario/views/"),("Dependencias del proyecto","pom.xml")])
    add_heading(doc,"Anexo B. Fuente de requisitos",2)
    add_p(doc,"Requisitos académicos consultados: “Req Proyecto Final.pdf”, proporcionado por el usuario. Este documento se elaboró con base en el estado actual del repositorio Sistema-Inventarios y debe actualizarse cuando se implementen los módulos pendientes.")

    # Keep headings with following content and save
    doc.core_properties.title="Proyecto Final - Sistema de Gestión de Inventarios"
    doc.core_properties.subject="Documento de análisis, diseño, implementación y pruebas"
    doc.core_properties.author="Oskar Luis Cabrera Chavero"
    doc.save(OUT)
    print(OUT)

if __name__ == "__main__": main()
