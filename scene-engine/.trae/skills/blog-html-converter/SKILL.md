---
name: "blog-html-converter"
description: "Converts Markdown blog posts to HTML with SVG diagrams. Invoke when user needs to convert blog content to HTML format for web publishing or WeChat official accounts."
---

# Blog HTML Converter

## Overview

This skill provides comprehensive guidelines for converting Markdown blog posts to HTML format with SVG diagram support, ensuring proper display on web platforms and WeChat official accounts.

## When to Invoke

- Converting Markdown blog posts to HTML format
- Creating web-ready blog content from Markdown source
- Preparing blog content for WeChat official account publishing
- Replacing ASCII diagrams with SVG images in blog posts
- Generating responsive HTML blog layouts

## Workflow

### 1. Markdown to HTML Conversion

#### Basic Conversion Steps

1. **Read Source Markdown**
   - Parse the Markdown file structure
   - Identify all sections, code blocks, tables, and diagrams
   - Note any special formatting requirements

2. **Create HTML Structure**
   ```html
   <!DOCTYPE html>
   <html lang="zh-CN">
   <head>
       <meta charset="UTF-8">
       <meta name="viewport" content="width=device-width, initial-scale=1.0">
       <title>{Blog Title}</title>
       <style>
           /* Responsive CSS variables */
           :root {
               --primary-color: #2563eb;
               --secondary-color: #1e40af;
               --bg-color: #f8fafc;
               --text-color: #334155;
               --code-bg: #f1f5f9;
               --border-color: #e2e8f0;
               --highlight-bg: #dbeafe;
           }
       </style>
   </head>
   <body>
       <!-- Content structure -->
   </body>
   </html>
   ```

3. **Content Mapping**
   | Markdown Element | HTML Element | CSS Class |
   |------------------|--------------|-----------|
   | `# Heading` | `<h1>` | `.header h1` |
   | `## Heading` | `<h2>` | `h2[data-section]` |
   | `### Heading` | `<h3>` | `h3` |
   | Code block | `<pre><code>` | `pre` |
   | Table | `<table>` | `table` |
   | Blockquote | `<blockquote>` | `blockquote` |
   | ASCII Diagram | `<img>` (SVG) | `.svg-diagram` |

### 2. ASCII to SVG Diagram Conversion

#### Why Convert to SVG?

- **Responsive**: Scales perfectly on all devices
- **WeChat Compatible**: Displays correctly in official accounts
- **Searchable**: Text within SVG is selectable
- **Small Size**: Compact vector format
- **Professional**: Clean, modern appearance

#### SVG Generation Guidelines

1. **File Naming Convention**
   ```
   diagram_{description}.svg
   Examples:
   - diagram_three_layer.svg
   - diagram_architecture_overview.svg
   - diagram_data_flow.svg
   ```

2. **SVG Template Structure**
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" width="{width}px" height="{height}px">
   <defs>
       <style>
           .bg { fill: #fafafa; }
           .box { fill: #ffffff; stroke: #2563eb; stroke-width: 2; rx: 8; }
           .box-alt { fill: #dbeafe; stroke: #1e40af; stroke-width: 2; rx: 8; }
           .box-highlight { fill: #fef3c7; stroke: #d97706; stroke-width: 2; rx: 8; }
           .title-box { fill: #2563eb; stroke: none; rx: 4; }
           .arrow { fill: none; stroke: #64748b; stroke-width: 2; marker-end: url(#arrowhead); }
           .text { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 13px; fill: #334155; }
           .text-title { font-size: 15px; font-weight: bold; fill: #1e40af; }
           .text-header { font-size: 16px; font-weight: bold; fill: #ffffff; }
           .text-small { font-size: 11px; fill: #64748b; }
       </style>
       <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
           <polygon points="0 0, 10 3.5, 0 7" fill="#64748b"/>
       </marker>
   </defs>
   <rect class="bg" width="100%" height="100%"/>
   <!-- Diagram content -->
   </svg>
   ```

3. **Common Diagram Components**

   **Title Box**
   ```xml
   <rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>
   <text class="text-header" x="400" y="38" text-anchor="middle">Diagram Title</text>
   ```

   **Content Box**
   ```xml
   <rect class="box" x="30" y="70" width="740" height="100"/>
   <text class="text-title" x="50" y="95">Section Title</text>
   <text class="text" x="50" y="120">Content description</text>
   ```

   **Arrow Connection**
   ```xml
   <line class="arrow" x1="400" y1="170" x2="400" y2="200"/>
   ```

   **Highlighted Box**
   ```xml
   <rect class="box-highlight" x="50" y="110" width="200" height="40" rx="6"/>
   <text class="text" x="150" y="135" text-anchor="middle" font-weight="bold">Important Info</text>
   ```

4. **Color Scheme**
   | Element | Fill | Stroke | Usage |
   |---------|------|--------|-------|
   | Primary Box | #ffffff | #2563eb | Main content boxes |
   | Alternate Box | #dbeafe | #1e40af | Secondary/emphasis |
   | Highlight Box | #fef3c7 | #d97706 | Important notes |
   | Title Box | #2563eb | none | Section headers |
   | Background | #fafafa | - | Canvas background |

5. **SVG Size Guidelines**
   | Diagram Type | Recommended Size |
   |--------------|------------------|
   | Simple flow | 700×340px |
   | Architecture | 800×490px |
   | Multi-layer | 700×510px |
   | Complex process | 800×580px |

### 3. HTML Integration

#### SVG Embedding

Replace ASCII diagram divs with SVG images:

```html
<!-- Before: ASCII Diagram -->
<div class="diagram">
┌─────────────────┐
│   ASCII Art     │
└─────────────────┘
</div>

<!-- After: SVG Diagram -->
<div class="svg-diagram">
    <img src="diagram_{name}.svg" alt="Description" 
         style="max-width: 100%; height: auto; border: 1px solid #e2e8f0; border-radius: 8px;"/>
</div>
```

#### Responsive Image Styling

```css
.svg-diagram {
    margin: 25px 0;
    text-align: center;
}

.svg-diagram img {
    max-width: 100%;
    height: auto;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
```

### 4. Complete CSS Template

```css
:root {
    --primary-color: #2563eb;
    --secondary-color: #1e40af;
    --bg-color: #f8fafc;
    --text-color: #334155;
    --code-bg: #f1f5f9;
    --border-color: #e2e8f0;
    --highlight-bg: #dbeafe;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    line-height: 1.8;
    color: var(--text-color);
    background-color: var(--bg-color);
}

.container {
    max-width: 1200px;
    margin: 0 auto;
    background: white;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

/* Header */
.header {
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
    color: white;
    padding: 60px 40px;
    text-align: center;
}

/* Navigation */
.nav {
    background: white;
    padding: 20px 40px;
    border-bottom: 2px solid var(--border-color);
    position: sticky;
    top: 0;
    z-index: 100;
}

/* Content */
.content { padding: 40px; }

h2 {
    font-size: 2rem;
    color: var(--primary-color);
    margin: 50px 0 25px;
    padding-bottom: 15px;
    border-bottom: 3px solid var(--primary-color);
}

h3 {
    font-size: 1.5rem;
    color: var(--secondary-color);
    margin: 35px 0 20px;
}

/* Code blocks */
pre {
    background: var(--code-bg);
    border: 1px solid var(--border-color);
    border-radius: 8px;
    padding: 20px;
    overflow-x: auto;
    margin: 25px 0;
    font-family: "Consolas", "Monaco", monospace;
    font-size: 0.9rem;
}

/* Tables */
table {
    width: 100%;
    border-collapse: collapse;
    margin: 25px 0;
    background: white;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

th {
    background: var(--primary-color);
    color: white;
    padding: 15px;
    text-align: left;
}

td {
    padding: 12px 15px;
    border-bottom: 1px solid var(--border-color);
}

/* SVG Diagrams */
.svg-diagram {
    margin: 25px 0;
    text-align: center;
}

.svg-diagram img {
    max-width: 100%;
    height: auto;
    border: 1px solid var(--border-color);
    border-radius: 8px;
}

/* Responsive */
@media (max-width: 768px) {
    .content { padding: 20px; }
    h2 { font-size: 1.5rem; }
}
```

### 5. File Organization

```
docs/
├── blog-post.md              # Source Markdown
├── blog-post.html            # Generated HTML
├── diagram_1.svg             # SVG diagrams
├── diagram_2.svg
└── generate_svgs.py          # Optional: SVG generation script
```

### 6. Quality Checklist

- [ ] All ASCII diagrams converted to SVG
- [ ] SVG files use consistent color scheme
- [ ] HTML is responsive (test on mobile)
- [ ] Images have proper alt text
- [ ] Code blocks have syntax highlighting classes
- [ ] Tables are readable on small screens
- [ ] Navigation works correctly
- [ ] Print styles are defined
- [ ] WeChat compatibility verified

## Example Usage

### Input: Markdown with ASCII Diagram

```markdown
## Architecture

```
┌─────────────┐
│   Layer 1   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Layer 2   │
└─────────────┘
```
```

### Output: HTML with SVG

```html
<h2>Architecture</h2>

<div class="svg-diagram">
    <img src="diagram_architecture.svg" alt="System Architecture" 
         style="max-width: 100%; height: auto; border: 1px solid #e2e8f0; border-radius: 8px;"/>
</div>
```

## Best Practices

1. **Always use relative paths** for SVG images
2. **Include alt text** for accessibility
3. **Test on mobile devices** before publishing
4. **Keep SVG files small** (optimize if needed)
5. **Use consistent styling** across all diagrams
6. **Version control** both Markdown and HTML
7. **Backup SVG generation scripts** for future edits

## Troubleshooting

| Issue | Solution |
|-------|----------|
| SVG not displaying | Check file path and extension |
| Text cut off in SVG | Increase viewBox dimensions |
| Colors inconsistent | Use CSS variables |
| Mobile layout broken | Add viewport meta tag |
| WeChat formatting issues | Use inline styles |
