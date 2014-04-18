/*
 * EECE 381 Module 2
 * Author: Team 13
 * Created: March 19, 2014
 * Last Modified: April 1, 2014
 */

#include "app_header.h"
#include "bitmap_platform.h"
#include "sound_platform.h"

int main()
{
	int i;
	unsigned char data;
	unsigned char parity;
	unsigned char revercedMessage[MAX];
	unsigned char originalMessage[MAX];
	unsigned char receivedMessage[MAX];
	int end; // end index
	int num_to_receive;
	int signal;
	int counter = 0;

	int client;
	int ready[4]={0,0,0,0};
	int count =0;


	// ---- Villain's Weakness ----
	struct weakness villain;
	// -----------------------

	unsigned char messageForApp[] = "ABCDEY";
	int mslength = strlen(messageForApp);
	//printf("Message length is %d\n", mslength );


	printf("UART Initialization\n");
	alt_up_rs232_dev* uart = alt_up_rs232_open_dev("/dev/rs232");

	// *** PLAYER ***
	struct player player;
	struct player player2;
	struct player player3;
	struct player player4;
	//player.life = 35;
	setRandomActions(&player, &player2,&player3,&player4);
	chooseWeakness(&player, &villain,0);


	chooseWeakness(&player2, &villain,1);


	chooseWeakness(&player3, &villain,2);


	chooseWeakness(&player4, &villain,3);

	player.life[0] = 0xFF; // broadcast to all clients
	player.life[1] = 5; //
	player.life[2] = 89; // Y

	villain.life = 5;

	printf("Set ID to %d\n", player.life[0] );

	//--------------I AM JOANNNA!!!!!!!!!!!! CONFIG STUFF-------------------------------------
	//--------Audio----------------------------------------------------
	printf("SDCARD CONNECTION CHECK\n");
	alt_up_sd_card_dev *device_reference=alt_up_sd_card_open_dev("/dev/Altera_UP_SD_Card_Avalon_Interface_0");
	alt_up_audio_dev *audio;
	sdcardcheck(device_reference);

	printf("AV CONFIG SETUP\n");
	av_config_setup();

	printf("AUDIO OPEN\n");
	audio=alt_up_audio_open_dev("/dev/audio");

	//--------------------Pixel Buffer-------------------------------------------------
	//define pixel_buffer
	alt_up_pixel_buffer_dma_dev* pixel_buffer;

	// Use the name of your pixel buffer DMA core
	pixel_buffer = alt_up_pixel_buffer_dma_open_dev("/dev/pixel_buffer_dma");

	//Define buffer addresses
	unsigned int pixel_buffer_addr1 = PIXEL_BUFFER_BASE;
	unsigned int pixel_buffer_addr2 = PIXEL_BUFFER_BASE + (320 * 240 * 2);

	//Set the first buffer address
	alt_up_pixel_buffer_dma_change_back_buffer_address(pixel_buffer,pixel_buffer_addr1);

	// Swap background and foreground buffers
	alt_up_pixel_buffer_dma_swap_buffers(pixel_buffer);
	// Wait for the swap to complete
	while(alt_up_pixel_buffer_dma_check_swap_buffers_status(pixel_buffer));

	//Set the 2nd buffer address
	alt_up_pixel_buffer_dma_change_back_buffer_address(pixel_buffer,pixel_buffer_addr2);

	// Clear the screen
	alt_up_pixel_buffer_dma_clear_screen(pixel_buffer, 0);
	alt_up_pixel_buffer_dma_clear_screen(pixel_buffer, 1);

	//--------char buffer!!!!!!!!! AUGHAUGHAUGHAUGHAUGH
	alt_up_char_buffer_dev *char_buffer;
	char_buffer = alt_up_char_buffer_open_dev("/dev/char_drawer");
	alt_up_char_buffer_init(char_buffer);
	//alt_up_char_buffer_clear(char_buffer); // Clear char buffer

	//Initiate all sounds up here and play down there
	//---INTRO SONG---//
	unsigned int intro_buf[INTRO_L];
	char *intro_name="intro.wav";
	short int intro_handle;

	//must have the songs read beforehand
	read_intro(intro_buf, intro_name, intro_handle);

	//---CLING SONG---//
	unsigned int buffer[CLING_L];
	char *filename="cling.wav";
	short int handle;

	//must have the songs read beforehand
	read_audio(buffer, filename, handle);
	//---PEW SONG---//
	unsigned int pew_buf[PEW_L];
	char *pew_name="pew.wav";
	short int pew_handle;

	//must have the songs read beforehand
	read_pew(pew_buf, pew_name, pew_handle);
	//---PVIC SONG---//
	unsigned int pvic_buf[PVIC_L];
	clearFIFO(audio,pvic_buf);
	char *pvic_name="pvic.wav";
	short int pvic_handle;

	//must have the songs read beforehand
	read_pvic(pvic_buf, pvic_name, pvic_handle);
	//---VVIC SONG---//
	unsigned int vvic_buf[VVIC_L];
	char *vvic_name="vvic.wav";
	short int vvic_handle;

	//must have the songs read beforehand
	read_vvic(vvic_buf, vvic_name, vvic_handle);

	//--------------END OF JO CONFIG STUFF-------------------------------------

	//------------EVIL MAN START SCREEN-------------------//
	//pictures will be okay as they overlap each other
	//title
	draw_title(pixel_buffer);
	draw_fire_sides(pixel_buffer);

	full_health_bar(pixel_buffer);
	full_phealth_bar(pixel_buffer);

	//faces
	draw_face(pixel_buffer);

	alt_up_char_buffer_string(char_buffer, "Welcome Heroes!", 1, 218);


	//---INTRO SONG---//
	//do the play_audio in the respective areas
	play_intro(audio, intro_buf, intro_handle);
	alt_up_sd_card_fclose(intro_name);
	draw_doomdevice(pixel_buffer);


	while(1) {
		printf("Number of life left is %d \n", player.life[1]);
		printf("Number of Villain life left is %d \n", villain.life);

		// Clear signal
		signal = 0;

		//clear char buffer
		alt_up_char_buffer_clear(char_buffer);
		alt_up_char_buffer_string(char_buffer, "Party Health: ", 1, 218);

		//printf("Clearing read buffer to start\n");
		while (alt_up_rs232_get_used_space_in_read_FIFO(uart)!=0) {
			alt_up_rs232_read_data(uart, &data, &parity);
			printf("the data is %d", data);
			printf("the number of bytes in the read buffer is %d\n", alt_up_rs232_get_used_space_in_read_FIFO(uart));
		}

		// ***** NIOS: receive message *****

		// Waiting
		printf("Waiting for data from the Middleman\n");
		while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0) {
			;
		}

		// First byte is the client ID
		alt_up_rs232_read_data(uart, &data, &parity);
		client = (int)data;
		printf("1. Client ID in char %c, int %d \n", data, client);

		delay();

		alt_up_rs232_read_data(uart, &data, &parity);
		printf("2. extra char in char %c the int for that it %d\n", data, data);

		delay();

		// Next byte is the number of characters in our message
		alt_up_rs232_read_data(uart, &data, &parity);
		num_to_receive = (int)data;
		printf("3. About to receive %d characters:\n", num_to_receive);

		delay();

		//alt_up_rs232_read_data(uart, &data, &parity);
		//alt_up_rs232_read_data(uart, &data, &parity);


		alt_up_rs232_read_data(uart, &data, &parity);
		signal = (int)data;
		printf("4. Signal in char %c the int for that it %d\n", data, data);

		delay();


		/***********************************initialize actions for players*****************/
		// 90 in dec, Z in ASCII
		if( signal == 90) {

			printf("Incoming request for ACTION\n");


			// ***** NIOS: send the message *****

			//printf("Clearing read buffer to start\n");
			//while (alt_up_rs232_get_available_space_in_write_FIFO(uart)!=0) {

			//}



			//---------------Client Checking-----------------------//
			if(client == 1){
				//printf("Sending the message to the Middleman\n");
				//printf("the number of bytes left in the FIFO before first byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				alt_up_rs232_write_data(uart, (unsigned char) client);
				//printf("the number of bytes left in the FIFO before second byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				alt_up_rs232_write_data(uart, (unsigned char) ACTIONS_NUMBER);
				//// Now send the actual message to the Middleman
				for (i = 0; i < ACTIONS_NUMBER; i++) {
					//printf("the number of bytes left in the FIFO before loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					alt_up_rs232_write_data(uart, player.action[i]);
					//printf("this is loop %d and the message is %c\n", i, messageForApp[i]);
					delay();
					//printf("the number of bytes left in the FIFO after loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				}
			}

			if(client == 2){
				//printf("Sending the message to the Middleman\n");
				//printf("the number of bytes left in the FIFO before first byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				alt_up_rs232_write_data(uart, (unsigned char) client);
				//printf("the number of bytes left in the FIFO before second byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				alt_up_rs232_write_data(uart, (unsigned char) ACTIONS_NUMBER);
				//// Now send the actual message to the Middleman
				for (i = 0; i < ACTIONS_NUMBER; i++) {
					//printf("the number of bytes left in the FIFO before loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					alt_up_rs232_write_data(uart, player2.action[i]);
					//printf("this is loop %d and the message is %c\n", i, messageForApp[i]);
					delay();
					//printf("the number of bytes left in the FIFO after loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				}
			}

			if(client == 3){
				//printf("Sending the message to the Middleman\n");
				//printf("the number of bytes left in the FIFO before first byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				alt_up_rs232_write_data(uart, (unsigned char) client);
				//printf("the number of bytes left in the FIFO before second byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				alt_up_rs232_write_data(uart, (unsigned char) ACTIONS_NUMBER);
				//// Now send the actual message to the Middleman
				for (i = 0; i < ACTIONS_NUMBER; i++) {
					//printf("the number of bytes left in the FIFO before loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					alt_up_rs232_write_data(uart, player3.action[i]);
					//printf("this is loop %d and the message is %c\n", i, messageForApp[i]);
					delay();
					//printf("the number of bytes left in the FIFO after loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				}
			}

			if(client == 4){
				//printf("Sending the message to the Middleman\n");
				//printf("the number of bytes left in the FIFO before first byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				alt_up_rs232_write_data(uart, (unsigned char) client);
				//printf("the number of bytes left in the FIFO before second byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				alt_up_rs232_write_data(uart, (unsigned char) ACTIONS_NUMBER);
				//// Now send the actual message to the Middleman
				for (i = 0; i < ACTIONS_NUMBER; i++) {
					//printf("the number of bytes left in the FIFO before loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					alt_up_rs232_write_data(uart, player4.action[i]);
					//printf("this is loop %d and the message is %c\n", i, messageForApp[i]);
					delay();
					//printf("the number of bytes left in the FIFO after loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
				}
			}
			printf("Finish incoming request for ACTION\n");
			signal = 0;
		}

		/*	else if(signal == 87){ // 87 in decimal, W in ASCII

			printf("Incoming request for LIFE\n");

			ready[client-1]=1;
			printf("count is %d",count);
			if(count==4){
			//printf("Sending the message to the Middleman\n");
			//printf("the number of bytes left in the FIFO before first byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
			alt_up_rs232_write_data(uart, (unsigned char) client);
			delay();
			alt_up_rs232_write_data(uart, (unsigned char) LIFE_SIZE-1);
			delay();
			//// Now send the actual message to the Middleman
			for (i = 1; i < LIFE_SIZE; i++) {
				alt_up_rs232_write_data(uart, player.life[i]);
				delay();
			}
			delay();

			signal = 0;
		}
		}*/

		else if(signal == 65 || signal == 66 || signal == 67 || signal == 68 || signal == 69 || signal == 70 || signal == 71 || signal == 72 || signal == 73 || signal == 74 || signal == 75 || signal == 76 || signal == 77 || signal == 78 || signal == 79|| signal == 80 || signal == 81 || signal == 82 || signal == 83 || signal == 84 || signal == 85) {
			printf("Check for weakness match\n");
			printf("Signal is %d\n", signal);

			// if hit weakness, then life stays the same
			// otherwise, then decrease life
			//health bar for players
			//alt_up_char_buffer_string(char_buffer, "Party Health: ", 1, 218);

			if(signal != villain.weakness[client-1]) {
				printf("in player miss loop\n");

				if( player.life[1] > 0){
					//evil face attack the lives of the innocent
					printf("1 player miss loop\n");
					draw_face(pixel_buffer);
					printf("2 player miss loop\n");
					//draw_doomdevice(pixel_buffer);
					printf("3 player miss loop\n");
					//lightning attack
					draw_light_sides(pixel_buffer);
					printf("4 player miss loop\n");
					//life reduce
					player.life[1]--;
					printf("5 player miss loop\n");
									if(player.life[1]==4)
									{
										phealth_bar_min1(pixel_buffer);
									}
									else if (player.life[1]==3)
									{
										phealth_bar_min2(pixel_buffer);
									}
									else if (player.life[1]== 2)
									{
										phealth_bar_min3(pixel_buffer);
									}
									else if (player.life[1]== 1)
									{
										phealth_bar_min4(pixel_buffer);
									}
									else{
										;
									}

					//---CLING SONG---//
					//do the play_audio in the respective areas
					play_audio(audio, buffer, handle);
					printf("6 player miss loop\n");
					alt_up_sd_card_fclose(filename);
					printf("7 player miss loop\n");

					//!!!!!!!!!NEED VILLAIN HEALTH BAR

				}
				printf("exit player zero hp loop\n");
			}

			else
			{
				printf("in player hit loop\n");
				draw_hurtface(pixel_buffer);
				//draw_doomdevice(pixel_buffer);
				draw_fire_sides(pixel_buffer);

				villain.life--;
				//health bars
				printf("villain life is %d\n", villain.life);
				if(villain.life==4)
				{
					health_bar_min1(pixel_buffer);
				}
				else if (villain.life==3)
				{
					health_bar_min2(pixel_buffer);
				}
				else if (villain.life== 2)
				{
					health_bar_min3(pixel_buffer);
				}
				else if (villain.life== 1)
				{
					health_bar_min4(pixel_buffer);
				}
				else{
					;
				}

				//---PEW SONG---//
				//do the play_audio in the respective areas
				play_pew(audio, pew_buf, pew_handle);
				alt_up_sd_card_fclose(pew_name);
			}
			if(villain.life==0)
			{
				draw_deadface(pixel_buffer);
				//draw_doomdevice(pixel_buffer);

				//empty health bar
				health_bar_min5(pixel_buffer);

				//---PVIC SONG---//
				//do the play_audio in the respective areas
				play_pvic(audio, pvic_buf, pvic_handle);
				alt_up_sd_card_fclose(pvic_name);
				int k=0;

				/*****send defeat message to ANDroid****/
				while(k!=3){
					//printf("Sending the message to the Middleman\n");
					//printf("the number of bytes left in the FIFO before first byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					alt_up_rs232_write_data(uart, (unsigned char) 0xFF);
					//printf("the number of bytes left in the FIFO before second byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					alt_up_rs232_write_data(uart, (unsigned char) 1);
					//// Now send the actual message to the Middleman
					for (i = 0; i < 1; i++) {
						//printf("the number of bytes left in the FIFO before loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
						alt_up_rs232_write_data(uart, 98);
						//printf("this is loop %d and the message is %c\n", i, messageForApp[i]);
						delay();
						//printf("the number of bytes left in the FIFO after loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					}
					long_delay();
					k++;
				}
				/********************resetting game**************/

				player.life[1]=5;
				setRandomActions(&player, &player2,&player3,&player4);
				chooseWeakness(&player, &villain,0);


				chooseWeakness(&player2, &villain,1);


				chooseWeakness(&player3, &villain,2);


				chooseWeakness(&player4, &villain,3);
				////finish sending
				full_health_bar(pixel_buffer);
				full_phealth_bar(pixel_buffer);
				draw_face(pixel_buffer);
				villain.life=5;
			}

			else if(player.life[1]==0)
			{
				//evil face attack the lives of the innocent
				draw_face(pixel_buffer);
				//draw_doomdevice(pixel_buffer);
				draw_fire_sides(pixel_buffer);

				phealth_bar_min5(pixel_buffer);

				//---VVIC SONG---//
				//do the play_audio in the respective areas
				play_vvic(audio, vvic_buf, vvic_handle);
				alt_up_sd_card_fclose(vvic_name);
				int k =0;

				/*****send defeat message to ANDroid****/
				while(k!=3){
					//printf("Sending the message to the Middleman\n");
					//printf("the number of bytes left in the FIFO before first byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					alt_up_rs232_write_data(uart, (unsigned char) 0xFF);
					//printf("the number of bytes left in the FIFO before second byte is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					alt_up_rs232_write_data(uart, (unsigned char) 1);
					//// Now send the actual message to the Middleman
					for (i = 0; i < 1; i++) {
						//printf("the number of bytes left in the FIFO before loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
						alt_up_rs232_write_data(uart, 97);
						//printf("this is loop %d and the message is %c\n", i, messageForApp[i]);
						delay();
						//printf("the number of bytes left in the FIFO after loop is %d\n", alt_up_rs232_get_available_space_in_write_FIFO(uart));
					}
					long_delay();
					k++;
				}
				/********************resetting game**************/

				player.life[1]=5;
				setRandomActions(&player, &player2,&player3,&player4);
				chooseWeakness(&player, &villain,0);


				chooseWeakness(&player2, &villain,1);


				chooseWeakness(&player3, &villain,2);


				chooseWeakness(&player4, &villain,3);

				full_health_bar(pixel_buffer);
				full_phealth_bar(pixel_buffer);
				draw_face(pixel_buffer);
				villain.life=5;
				////finish reseting
			}

			//char needs fixing!
			//health bar for players
			//alt_up_char_buffer_string(char_buffer, "Party Health: ", 1, 218);

			//conversion of int to char
			//char converted[100];
			//sprintf(converted, "%d", player.life[1]);
			//alt_up_char_buffer_string(char_buffer, converted, 25, 218);



		}
	}
	return 0;

}


/*
 * Set the actions (from 0 to 19) for a given player.
 * Convert actions to capital letters in ASCII.
 */
void setRandomActionsLetters(struct player* pl) {

	struct player* p = (struct player*) pl;
	int i;
	int randomNum;

	for(i=0; i < ACTIONS_NUMBER - 1; i++) {
		randomNum = (rand() %20) + 65;
		p->action[i] = randomNum; // convert to ASCII Capital Letters
	}

	p->action[ACTIONS_NUMBER - 1] = 89; // encode Y in ASCII

	printf("Random actions: ");

	for(i=0; i < ACTIONS_NUMBER; i++) {
		printf("%d ", p->action[i]);
	}
	printf("\n");
}


/*
 * Set the actions (from 0 to 19) for a given player.
 * Convert actions to integers in ASCII.
 */
void setRandomActions(struct player* pl, struct player* p2, struct player* p3,struct player* p4) {

	struct player* player = (struct player*) pl;
	struct player* player2 = (struct player*) p2;
	struct player* player3 = (struct player*) p3;
	struct player* player4 = (struct player*) p4;
	int i;
	int array[20];
	int randomNum;

	//initialize the array to have from 0 to 19
	for(i=0;i<20;i++)
	{
		array[i]=i+65;//ascii chars
	}

	for(i=0;i<20;i++)
	{
		int temp;
		int num;
		num=rand()%20;
		temp=array[i];
		array[i]=array[num];
		array[num]=temp;
	}


	for(i=0;i<5;i++){
		player->action[i]=array[i];
		player2->action[i]=array[i+5];
		player3->action[i]=array[i+10];
		player4->action[i]=array[i+15];
	}


	player->action[ACTIONS_NUMBER - 1] = 89; // encode Y in ASCII
	player2->action[ACTIONS_NUMBER - 1] = 89; // encode Y in ASCII
	player3->action[ACTIONS_NUMBER - 1] = 89; // encode Y in ASCII
	player4->action[ACTIONS_NUMBER - 1] = 89; // encode Y in ASCII
	//p->action[0] = 0xFF; // broadcast to all clients
	/*old way
	for(i=1; i < ACTIONS_NUMBER - 1; i++) {
		randomNum = rand() %20;
		p->action[i] = randomNum + 30; // convert to ASCII
	}

	p->action[ACTIONS_NUMBER - 1] = 89; // encode Y in ASCII

	printf("Random actions: ");

	for(i=0; i < ACTIONS_NUMBER; i++) {
		printf("%d ", p->action[i]);
	}
	printf("\n");
	 */
}

/*
 * Choose a villain's weakness from each player's set of actions.
 * Store the result in villain's struct.
 */
void chooseWeakness(struct player* pl, struct weakness* vln, int id) {

	struct player* p = (struct player*) pl;
	struct weakness* v = (struct weakness*) vln;

	int randomIndex = rand() % 5;

	v->weakness[id] = p->action[randomIndex];

	printf("Chose %d for weakness\n", v->weakness[id]);
}

void delay(){
	int i;
	for(i=0; i<8000; i ++){
		;

	}
}

void long_delay(){
	int i;
	for(i=0; i<5000000; i ++){
		;

	}
}

