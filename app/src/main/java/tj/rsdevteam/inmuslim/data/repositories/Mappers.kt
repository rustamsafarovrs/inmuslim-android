package tj.rsdevteam.inmuslim.data.repositories

import tj.rsdevteam.inmuslim.data.models.Message
import tj.rsdevteam.inmuslim.data.models.Region
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.data.models.User
import tj.rsdevteam.inmuslim.data.models.api.MessageDTO
import tj.rsdevteam.inmuslim.data.models.api.RegionDTO
import tj.rsdevteam.inmuslim.data.models.api.RegisterUserDTO
import tj.rsdevteam.inmuslim.data.models.api.TimingDTO
import tj.rsdevteam.inmuslim.data.models.api.UpdateMessagingIdDTO

/**
 * Created by Rustam Safarov on 6/25/24.
 * github.com/rustamsafarovrs
 */

fun RegionDTO.toRegion(): Region {
    return Region(
        id = this.id,
        name = this.name,
    )
}

fun TimingDTO.toTiming(): Timing {
    return Timing(
        fajr = this.fajr,
        sunrise = this.sunrise,
        zuhr = this.zuhr,
        asr = this.asr,
        sunset = this.sunset,
        maghrib = this.maghrib,
        isha = this.isha,
    )
}

fun MessageDTO.toMessage(): Message {
    return Message(
        message = this.msg,
    )
}

fun RegisterUserDTO.toUser(): User {
    return User(
        id = this.id,
    )
}

fun UpdateMessagingIdDTO.toMessage(): Message {
    return Message(
        message = this.msg,
    )
}
