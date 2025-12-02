package dev.esan.sla_app.utils

import dev.esan.sla_app.data.remote.dto.alertas.AlertaDto

object EmailFormatter {

    fun generateHtmlReport(alertas: List<AlertaDto>): String {
        val rows = alertas.joinToString(separator = "") { alerta ->
            """
            <tr>
                <td style="padding: 8px; border: 1px solid #ddd;">${alerta.fecha}</td>
                <td style="padding: 8px; border: 1px solid #ddd;">${alerta.mensaje}</td>
                <td style="padding: 8px; border: 1px solid #ddd;">${alerta.rol}</td>
                <td style="padding: 8px; border: 1px solid #ddd;">${alerta.porcentaje}%</td>
            </tr>
            """.trimIndent()
        }

        return """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Reporte de Alertas SLA</h2>
                <p>A continuación se detallan las alertas registradas en el sistema:</p>
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="background-color: #f2f2f2;">
                            <th style="padding: 8px; border: 1px solid #ddd; text-align: left;">Fecha</th>
                            <th style="padding: 8px; border: 1px solid #ddd; text-align: left;">Mensaje</th>
                            <th style="padding: 8px; border: 1px solid #ddd; text-align: left;">Rol</th>
                            <th style="padding: 8px; border: 1px solid #ddd; text-align: left;">%</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rows
                    </tbody>
                </table>
                <p style="margin-top: 20px; font-size: 12px; color: #888;">Este reporte fue generado automáticamente.</p>
            </body>
            </html>
        """.trimIndent()
    }
}
