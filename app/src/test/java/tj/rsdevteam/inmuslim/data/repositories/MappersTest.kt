package tj.rsdevteam.inmuslim.data.repositories

import org.junit.Assert.assertEquals
import org.junit.Test
import tj.rsdevteam.inmuslim.data.models.Message
import tj.rsdevteam.inmuslim.data.models.Region
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.data.models.User
import tj.rsdevteam.inmuslim.data.models.api.MessageDTO
import tj.rsdevteam.inmuslim.data.models.api.RegionDTO
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserDTO
import tj.rsdevteam.inmuslim.data.models.api.TimingDTO
import tj.rsdevteam.inmuslim.data.models.api.UpdateMessagingIdDTO

class MappersTest {

    @Test
    fun `maps a region`() {
        assertEquals(Region(id = 1, name = "Dushanbe"), RegionDTO(id = 1, name = "Dushanbe").toRegion())
    }

    @Test
    fun `keeps every prayer time in its own field`() {
        val dto = TimingDTO(
            fajr = "04:00",
            sunrise = "05:30",
            zuhr = "12:30",
            asr = "16:30",
            sunset = "19:20",
            maghrib = "19:30",
            isha = "21:00",
        )

        assertEquals(
            Timing(
                fajr = "04:00",
                sunrise = "05:30",
                zuhr = "12:30",
                asr = "16:30",
                sunset = "19:20",
                maghrib = "19:30",
                isha = "21:00",
            ),
            dto.toTiming(),
        )
    }

    @Test
    fun `maps a registered user`() {
        assertEquals(User(id = 42), RegisterUserDTO(result = 0, msg = "", id = 42).toUser())
    }

    @Test
    fun `keeps the server message`() {
        assertEquals(Message("ok"), MessageDTO(result = 0, msg = "ok").toMessage())
        assertEquals(Message("updated"), UpdateMessagingIdDTO(result = 0, msg = "updated").toMessage())
    }
}
