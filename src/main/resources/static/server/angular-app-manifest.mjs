
export default {
  bootstrap: () => import('./main.server.mjs').then(m => m.default),
  inlineCriticalCss: true,
  baseHref: '/',
  locale: undefined,
  routes: [
  {
    "renderMode": 0,
    "redirectTo": "/pacientes",
    "route": "/"
  },
  {
    "renderMode": 0,
    "route": "/login"
  },
  {
    "renderMode": 0,
    "route": "/pacientes"
  },
  {
    "renderMode": 0,
    "route": "/pacientes/nuevo"
  },
  {
    "renderMode": 0,
    "route": "/pacientes/editar/*"
  },
  {
    "renderMode": 0,
    "route": "/historias"
  },
  {
    "renderMode": 0,
    "route": "/turnos"
  },
  {
    "renderMode": 0,
    "route": "/visitas"
  },
  {
    "renderMode": 0,
    "route": "/dashboard"
  },
  {
    "renderMode": 0,
    "route": "/usuarios"
  },
  {
    "renderMode": 0,
    "route": "/respaldos"
  },
  {
    "renderMode": 0,
    "route": "/ayuda"
  },
  {
    "renderMode": 0,
    "redirectTo": "/pacientes",
    "route": "/**"
  }
],
  entryPointToBrowserMapping: undefined,
  assets: {
    'index.csr.html': {size: 961, hash: '652f05d3f657fa8b3550231755716eb207f970844ff644d6b496a853fbe9a6c5', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 1018, hash: '13cbb581ce9288d6952fc303dc14275b01e8f1ffe8c0f67825c83b515a4b5484', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'styles-DPCYYYVG.css': {size: 99916, hash: 'ZByzvgAE4jE', text: () => import('./assets-chunks/styles-DPCYYYVG_css.mjs').then(m => m.default)}
  },
};
