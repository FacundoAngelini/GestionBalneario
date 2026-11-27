
package com.Gestion.MiBalnearioGestion.Common.Email;

import java.util.List;

final class EmailHtml {

    private static final String AZUL = "#102F46";
    private static final String TURQUESA = "#28A99D";
    private static final String ARENA = "#E9C9A0";

    private EmailHtml() {
    }

    record Dato(String etiqueta, String valor) {
    }

    static String crear(
            String preheader,
            String titulo,
            String subtitulo,
            String saludo,
            String introduccion,
            List<Dato> datos,
            String textoBoton,
            String urlBoton,
            String advertencia
    ) {
        StringBuilder filas = new StringBuilder();

        if (datos != null) {
            for (Dato dato : datos) {
                filas.append("""
                    <tr>
                      <td style="padding:12px 14px;border-bottom:1px solid #E9EEF1;
                                 color:#687985;font-size:13px;width:38%%;">
                        %s
                      </td>
                      <td style="padding:12px 14px;border-bottom:1px solid #E9EEF1;
                                 color:%s;font-size:14px;font-weight:600;">
                        %s
                      </td>
                    </tr>
                    """.formatted(
                        escapar(dato.etiqueta()),
                        AZUL,
                        escapar(dato.valor())
                ));
            }
        }

        String boton = "";

        if (textoBoton != null && urlBoton != null) {
            boton = """
                <table role="presentation" cellspacing="0" cellpadding="0"
                       border="0" style="margin:28px auto 8px;">
                  <tr>
                    <td align="center" bgcolor="%s"
                        style="border-radius:7px;">
                      <a href="%s"
                         style="display:inline-block;padding:15px 28px;
                                color:#FFFFFF;text-decoration:none;
                                font-size:14px;font-weight:700;
                                letter-spacing:.3px;">
                        %s
                      </a>
                    </td>
                  </tr>
                </table>
                """.formatted(
                    TURQUESA,
                    escapar(urlBoton),
                    escapar(textoBoton)
            );
        }

        String bloqueAdvertencia = "";

        if (advertencia != null && !advertencia.isBlank()) {
            bloqueAdvertencia = """
                <table role="presentation" width="100%%" cellspacing="0"
                       cellpadding="0" border="0"
                       style="margin-top:24px;background:#FFF8ED;
                              border-left:4px solid %s;">
                  <tr>
                    <td style="padding:15px 17px;color:#765B35;
                               font-size:13px;line-height:1.6;">
                      %s
                    </td>
                  </tr>
                </table>
                """.formatted(ARENA, escapar(advertencia));
        }

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <meta name="color-scheme" content="light">
              <title>%s</title>
            </head>

            <body style="margin:0;padding:0;background:#F3F6F7;
                         font-family:Arial,Helvetica,sans-serif;color:%s;">

              <div style="display:none;max-height:0;overflow:hidden;
                          opacity:0;color:transparent;">
                %s
              </div>

              <table role="presentation" width="100%%" cellspacing="0"
                     cellpadding="0" border="0" bgcolor="#F3F6F7">
                <tr>
                  <td align="center" style="padding:36px 14px;">

                    <table role="presentation" width="100%%" cellspacing="0"
                           cellpadding="0" border="0"
                           style="max-width:600px;background:#FFFFFF;
                                  border-radius:12px;overflow:hidden;">

                      <!-- Encabezado ARENA -->
                      <tr>
                        <td bgcolor="%s"
                            style="padding:27px 34px 25px;
                                   border-bottom:4px solid %s;">

                          <div style="font-size:11px;color:#D5E3E9;
                                      letter-spacing:3px;font-weight:600;">
                            BALNEARIO
                          </div>

                          <div style="margin-top:5px;color:#FFFFFF;
                                      font-size:29px;font-weight:700;
                                      letter-spacing:4px;">
                            ARENA
                          </div>

                          <div style="margin-top:7px;color:#D5E3E9;
                                      font-size:12px;letter-spacing:.5px;">
                            Tu día de playa, sin vueltas.
                          </div>
                        </td>
                      </tr>

                      <!-- Contenido -->
                      <tr>
                        <td style="padding:34px 34px 30px;">

                          <div style="font-size:11px;font-weight:700;
                                      color:%s;letter-spacing:1.5px;
                                      text-transform:uppercase;">
                            %s
                          </div>

                          <h1 style="margin:12px 0 8px;color:%s;
                                     font-size:25px;line-height:1.3;
                                     font-weight:700;">
                            %s
                          </h1>

                          <p style="margin:0 0 25px;color:#71818B;
                                    font-size:14px;line-height:1.6;">
                            %s
                          </p>

                          <p style="margin:0 0 12px;color:%s;
                                    font-size:15px;line-height:1.6;">
                            %s
                          </p>

                          <p style="margin:0 0 25px;color:#536570;
                                    font-size:14px;line-height:1.7;">
                            %s
                          </p>

                          %s

                          %s

                          %s

                        </td>
                      </tr>

                      <!-- Pie -->
                      <tr>
                        <td bgcolor="#F7F9FA"
                            style="padding:23px 30px;text-align:center;
                                   border-top:1px solid #E9EEF1;">

                          <p style="margin:0 0 7px;color:%s;
                                    font-size:12px;font-weight:700;
                                    letter-spacing:1.5px;">
                            ARENA
                          </p>

                          <p style="margin:0;color:#84929A;
                                    font-size:11px;line-height:1.7;">
                            Este correo fue generado automáticamente.
                            <br>
                            Gracias por elegirnos.
                          </p>
                        </td>
                      </tr>

                    </table>

                  </td>
                </tr>
              </table>

            </body>
            </html>
            """.formatted(
                escapar(titulo),
                AZUL,
                escapar(preheader),
                AZUL,
                ARENA,
                TURQUESA,
                escapar(subtitulo),
                AZUL,
                escapar(titulo),
                escapar(saludo),
                escapar(introduccion),
                crearTabla(filas.toString()),
                boton,
                bloqueAdvertencia,
                AZUL
        );
    }

    private static String crearTabla(String filas) {
        if (filas == null || filas.isBlank()) {
            return "";
        }

        return """
            <table role="presentation" width="100%%" cellspacing="0"
                   cellpadding="0" border="0"
                   style="margin-top:20px;border:1px solid #E9EEF1;
                          border-radius:8px;border-collapse:separate;
                          overflow:hidden;">
              %s
            </table>
            """.formatted(filas);
    }

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
