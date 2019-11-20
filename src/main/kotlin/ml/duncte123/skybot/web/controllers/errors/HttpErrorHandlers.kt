/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017 - 2019  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ml.duncte123.skybot.web.controllers.errors

import com.fasterxml.jackson.databind.ObjectMapper
import me.duncte123.botcommons.web.ContentType
import ml.duncte123.skybot.objects.WebVariables
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.template.jtwig.JtwigTemplateEngine

object HttpErrorHandlers {

    fun notFound(request: Request, response: Response, engine: JtwigTemplateEngine, mapper: ObjectMapper): Any {
        if (request.headers("Accept") != ContentType.JSON.type ||
            response.type() != ContentType.JSON.type) {
            response.type(ContentType.TEXT_HTML.type)

            val modelAndView = WebVariables()
                .put("title", "404 - Page Not Found")
                .toModelAndView("errors/404.twig")

            return engine.render(modelAndView)
        }

        response.type(ContentType.JSON.type)

        return mapper.createObjectNode()
            .put("status", "failure")
            .put("message", "'${request.pathInfo()}' was not found")
            .put("code", response.status())
    }

    fun internalServerError(request: Request, response: Response, engine: JtwigTemplateEngine, mapper: ObjectMapper): Any {
        if (request.headers("Accept") != ContentType.JSON.type ||
            response.type() != ContentType.JSON.type) {
            response.type(ContentType.TEXT_HTML.type)

            val modelAndView = WebVariables()
                .put("title", "500 - Internal Server error")
                .toModelAndView("errors/500.twig")

            return engine.render(modelAndView)
        }

        response.type(ContentType.JSON.type)

        return mapper.createObjectNode()
            .put("status", "failure")
            .put("message", "Internal server error")
            .put("code", response.status())
    }

}
