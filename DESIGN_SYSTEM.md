# Gestor de Asistencia — Sistema de Diseño v2.0

## Filosofía Visual

Estilo **"Clean Dashboard"**: interfaz luminosa con espacios amplios, sombras sutiles y un único color de acento (Indigo) que guía la atención. Todo lo demás queda neutro para que los datos sean protagonistas.

---

## 1. Paleta de Colores

### Acento Principal — Indigo
| Token | Hex | Uso |
|-------|-----|-----|
| PRIMARY | `#6366F1` | Botones principales, borde focus, sidebar activo |
| PRIMARY-HOVER | `#4F46E5` | Hover de botones primarios |
| PRIMARY-LIGHT | `#EEF2FF` | Fondos de cards destacadas, hover filas tabla |
| PRIMARY-SOFT | `#C7D2FE` | Bordes de badges, acentos suaves |

### Superficies
| Token | Hex | Uso |
|-------|-----|-----|
| SURFACE | `#FFFFFF` | Cards, inputs, tabla |
| BACKGROUND | `#F8FAFC` | Fondo general de la app |
| BORDER | `#E2E8F0` | Bordes de componentes |

### Texto
| Token | Hex | Uso |
|-------|-----|-----|
| TEXT-PRIMARY | `#1E293B` | Texto principal |
| TEXT-SECONDARY | `#64748B` | Subtítulos, labels |
| TEXT-MUTED | `#94A3B8` | Placeholders, hints |

### Semánticos
| Estado | Color | Fondo | Texto |
|--------|-------|-------|-------|
| Éxito / Presente | `#10B981` | `#ECFDF5` | `#065F46` |
| Peligro / Falta | `#EF4444` | `#FEF2F2` | `#991B1B` |
| Alerta / Retraso | `#F59E0B` | `#FFFBEB` | `#92400E` |
| Neutral / Justificada | `#F1F5F9` | `#F1F5F9` | `#475569` |

---

## 2. Tipografía

**Fuente:** `Segoe UI` → `Roboto` → `Open Sans` → sans-serif

| Clase CSS | Tamaño | Peso | Uso |
|-----------|--------|------|-----|
| `.title-xl` | 28px | 700 | Título de página |
| `.title-lg` | 22px | 600 | Título de sección/card |
| `.title-md` | 18px | 600 | Subtítulos |
| `.text-secondary` | 13px | 400 | Texto de apoyo |
| `.text-muted` | 12px | 400 | Hints, placeholders |
| `.text-sm` | 12px | 400 | Badges, tags |
| (base) | 14px | 400 | Texto general |

---

## 3. Componentes Clave

### Botones
Todos los botones tienen `border-radius: 8px` y sombra sutil. Variantes:

| Clase CSS | Uso |
|-----------|-----|
| `.btn-primary` | Acción principal (Guardar, Entrar) |
| `.btn-success` | Confirmar, marcar presentes |
| `.btn-danger` | Eliminar |
| `.btn-warning` | Acciones de precaución |
| `.btn-ghost` | Cerrar sesión, acciones terciarias |
| `.btn-outline-primary` | Alternativa no rellena del primario |
| `.btn-lg` | Combinable: hace el botón más grande |
| `.btn-sm` | Combinable: hace el botón más pequeño |
| `.btn-icon` | Combinable: ajusta padding para icono+texto |

**Ejemplo FXML:**
```xml
<Button text="Guardar" styleClass="btn-primary, btn-lg, btn-icon">
    <graphic>
        <FontIcon iconLiteral="mdal-save" styleClass="icon-white"/>
    </graphic>
</Button>
```

### Inputs
Minimalistas, borde gris fino. Al hacer focus, borde Indigo con glow sutil.

### Cards
Fondo blanco, `border-radius: 12px`, sombra `dropshadow` muy ligera. Tres variantes:
- `.card` — Card estándar con padding 24px
- `.card-flush` — Sin padding (para envolver tablas)
- `.card-primary` — Fondo Indigo claro (destacar info)

### Tabla
Cabecera gris claro con texto uppercase pequeño. Filas alternadas sutiles. Hover en Indigo claro. Scrollbar minimalista (8px, sin flechas).

---

## 4. Iconografía — Ikonli Material 2

**REGLA CLAVE: A-L → `mdal-` | M-Z → `mdmz-`**

| Acción | iconLiteral | Rango |
|--------|-------------|-------|
| Login / Entrar | `mdal-arrow_forward` | A-L |
| Usuario / Perfil | `mdmz-person` | M-Z |
| Asistencia / Check | `mdal-fact_check` | A-L |
| Guardar | `mdmz-save` | M-Z |
| Eliminar | `mdal-delete_outline` | A-L |
| Añadir | `mdal-add_circle_outline` | A-L |
| Estadísticas | `mdal-bar_chart` | A-L |
| Cerrar sesión | `mdal-logout` | A-L |
| Calendario/Fecha | `mdal-calendar_today` | A-L |
| Grupo/Clase | `mdal-groups` | A-L |
| Asignatura | `mdmz-menu_book` | M-Z |
| Refrescar | `mdmz-refresh` | M-Z |
| Presente | `mdal-check_circle_outline` | A-L |
| Falta | `mdal-cancel` | A-L |
| Retraso | `mdmz-schedule` | M-Z |
| Justificada | `mdal-description` | A-L |
| Dashboard | `mdal-dashboard` | A-L |
| Admin/Registrar | `mdal-admin_panel_settings` | A-L |
| Escuela/Logo | `mdmz-school` | M-Z |

**Uso en FXML:**
```xml
<FontIcon iconLiteral="mdmz-save" styleClass="icon-md, icon-white"/>
```

---

## 5. Layout Propuesto (Paso 2)

```
┌──────────┬──────────────────────────────────────────┐
│          │  TOOLBAR: Título + Acciones              │
│ SIDEBAR  ├──────────────────────────────────────────┤
│          │  FILTER BAR (card con filtros)            │
│  Logo    ├──────────────────────────────────────────┤
│  Nav     │  STAT CARDS (fila de 4 minicards)        │
│  items   ├──────────────────────────────────────────┤
│          │  TABLE CARD (card-flush con TableView)   │
│          ├──────────────────────────────────────────┤
│          │  FOOTER: botones guardar                 │
└──────────┴──────────────────────────────────────────┘
```

El sidebar oscuro (`#1E293B`) contrasta con el contenido claro, creando jerarquía visual inmediata.
