package kr.pincoin.api.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverterFactory(StringToEnumConverterFactory())
    }
}

class StringToEnumConverterFactory : ConverterFactory<String, Enum<*>> {
    override fun <T : Enum<*>> getConverter(targetType: Class<T>): Converter<String, T> =
        StringToEnumConverter(targetType)
}

class StringToEnumConverter<T : Enum<*>>(
    private val enumType: Class<T>
) : Converter<String, T> {
    override fun convert(source: String): T =
        enumType.enumConstants.first { it.name.equals(source, ignoreCase = true) }
}